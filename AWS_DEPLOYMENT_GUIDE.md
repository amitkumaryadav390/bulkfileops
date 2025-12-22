# 🚀 AWS EC2 Deployment Guide - FileCreator Application

## Overview
Deploy both Spring Boot backend and React frontend on AWS EC2 with reverse proxy setup.

## Architecture
```
Internet → AWS EC2 → Nginx (Port 80/443) → {
    /api/* → Spring Boot Backend (Port 8080)
    /* → React Frontend (Static Files)
}
```

## Pre-Deployment Checklist
- [ ] AWS Account with billing enabled
- [ ] Domain name (optional but recommended)
- [ ] SSL certificate (Let's Encrypt recommended)
- [ ] GitHub repository with your code

---

# Phase 1: AWS EC2 Setup

## Step 1: Launch EC2 Instance

### 1.1 Instance Configuration
```
- AMI: Ubuntu Server 22.04 LTS (Free Tier Eligible)
- Instance Type: t2.micro (Free Tier) or t3.small (Production)
- Key Pair: Create new or use existing
- Storage: 8-20 GB (depending on usage)
```

### 1.2 Security Group Rules
```
Type        Protocol    Port Range    Source
SSH         TCP         22           Your IP (0.0.0.0/0 for initial setup)
HTTP        TCP         80           0.0.0.0/0
HTTPS       TCP         443          0.0.0.0/0
Custom TCP  TCP         8080         127.0.0.1/32 (localhost only)
Custom TCP  TCP         3000         127.0.0.1/32 (localhost only)
```

### 1.3 Launch and Connect
```bash
# Connect to your instance
ssh -i "your-key.pem" ubuntu@your-ec2-public-ip
```

---

# Phase 2: Server Environment Setup

## Step 2: Update System and Install Dependencies

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 11 (for Spring Boot)
sudo apt install openjdk-11-jdk -y
java -version

# Install Node.js 18+ (for React build)
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
node --version
npm --version

# Install Maven
sudo apt install maven -y
mvn -version

# Install Nginx (reverse proxy)
sudo apt install nginx -y

# Install Git
sudo apt install git -y

# Install PM2 (process manager)
sudo npm install -g pm2

# Install certbot (for SSL)
sudo apt install certbot python3-certbot-nginx -y
```

---

# Phase 3: Application Deployment

## Step 3: Clone and Build Applications

### 3.1 Clone Repository
```bash
# Create application directory
sudo mkdir -p /var/www/filecreator
sudo chown ubuntu:ubuntu /var/www/filecreator
cd /var/www/filecreator

# Clone your repository
git clone https://github.com/amitkumaryadav390/quizuploader.git .
# Note: Update this URL to your actual repository
```

### 3.2 Build Backend (Spring Boot)
```bash
# Navigate to backend
cd /var/www/filecreator

# Create production application properties
sudo tee src/main/resources/application-prod.properties > /dev/null <<EOF
# Production Configuration
server.port=8080
server.address=127.0.0.1

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# CORS Configuration
management.endpoints.web.cors.allowed-origins=http://your-domain.com,https://your-domain.com
management.endpoints.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
management.endpoints.web.cors.allowed-headers=*

# Logging Configuration
logging.level.com.example.filecreator=INFO
logging.level.org.springframework.web=WARN
logging.file.name=/var/log/filecreator/application.log
EOF

# Create log directory
sudo mkdir -p /var/log/filecreator
sudo chown ubuntu:ubuntu /var/log/filecreator

# Build the application
mvn clean package -DskipTests

# Verify JAR file exists
ls -la target/*.jar
```

### 3.3 Build Frontend (React)
```bash
# Navigate to frontend
cd /var/www/filecreator/frontend

# Update API URLs for production
# Edit src/components/ProcessingOptions.js
sudo tee -a production-config.js > /dev/null <<EOF
// Update these URLs in ProcessingOptions.js for production
const API_BASE_URL = window.location.origin; // Uses same domain
const INDIVIDUAL_ENDPOINT = '\${API_BASE_URL}/api/generate-docs';
const AGGREGATED_ENDPOINT = '\${API_BASE_URL}/api/generate-aggregated-docs';
EOF

# Install dependencies and build
npm install
npm run build

# Verify build directory
ls -la dist/
```

---

# Phase 4: Process Management Setup

## Step 4: Setup PM2 for Backend

### 4.1 Create PM2 Configuration
```bash
# Create PM2 ecosystem file
cd /var/www/filecreator
sudo tee ecosystem.config.js > /dev/null <<EOF
module.exports = {
  apps: [{
    name: 'filecreator-backend',
    script: 'java',
    args: [
      '-jar',
      '-Dspring.profiles.active=prod',
      '-Xmx512m',
      'target/filecreator-0.0.1-SNAPSHOT.jar'
    ],
    cwd: '/var/www/filecreator',
    instances: 1,
    autorestart: true,
    watch: false,
    max_memory_restart: '1G',
    env: {
      NODE_ENV: 'production',
      PORT: 8080
    },
    log_file: '/var/log/filecreator/combined.log',
    out_file: '/var/log/filecreator/out.log',
    error_file: '/var/log/filecreator/error.log',
    time: true
  }]
};
EOF
```

### 4.2 Start Backend with PM2
```bash
# Start the backend
pm2 start ecosystem.config.js

# Check status
pm2 status

# View logs
pm2 logs filecreator-backend

# Setup PM2 to start on boot
pm2 startup
pm2 save
```

---

# Phase 5: Nginx Configuration

## Step 5: Setup Reverse Proxy

### 5.1 Create Nginx Configuration
```bash
# Remove default configuration
sudo rm /etc/nginx/sites-enabled/default

# Create new site configuration
sudo tee /etc/nginx/sites-available/filecreator > /dev/null <<EOF
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;  # Replace with your domain or EC2 IP
    
    # Security headers
    add_header X-Content-Type-Options nosniff;
    add_header X-Frame-Options DENY;
    add_header X-XSS-Protection "1; mode=block";
    
    # Serve React frontend (static files)
    location / {
        root /var/www/filecreator/frontend/dist;
        index index.html;
        try_files \$uri \$uri/ /index.html;
        
        # Cache static assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
    
    # Proxy API requests to Spring Boot backend
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # Handle large file uploads
        client_max_body_size 10M;
        proxy_read_timeout 300s;
        proxy_connect_timeout 60s;
        proxy_send_timeout 300s;
    }
    
    # Health check endpoint
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
EOF

# Enable the site
sudo ln -s /etc/nginx/sites-available/filecreator /etc/nginx/sites-enabled/

# Test nginx configuration
sudo nginx -t

# Start and enable nginx
sudo systemctl start nginx
sudo systemctl enable nginx

# Restart nginx
sudo systemctl restart nginx
```

---

# Phase 6: SSL Certificate (Optional but Recommended)

## Step 6: Setup Let's Encrypt SSL

```bash
# Only run this if you have a domain name
# Replace your-domain.com with your actual domain

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Test auto-renewal
sudo certbot renew --dry-run

# Setup auto-renewal cron job
echo "0 12 * * * /usr/bin/certbot renew --quiet" | sudo crontab -
```

---

# Phase 7: Production Updates

## Step 7: Update Frontend URLs

### 7.1 Update React App for Production
```bash
# Edit ProcessingOptions.js to use relative URLs
cd /var/www/filecreator/frontend
sudo tee src/components/ProcessingOptions.js.production > /dev/null <<EOF
// Replace the hardcoded URLs with:
const endpoint = type === 'aggregated' 
  ? '/api/generate-aggregated-docs'  // Note: no localhost
  : '/api/generate-docs';

const response = await axios.post(endpoint, formData, {
  headers: {
    'Content-Type': 'multipart/form-data',
  },
  responseType: 'blob',
});
EOF

# Rebuild frontend with updated URLs
npm run build

# Copy to nginx directory (if needed)
sudo cp -r dist/* /var/www/filecreator/frontend/dist/
```

### 7.2 Update CORS in Backend
```bash
# Update WebConfig.java to allow your domain
cd /var/www/filecreator
sudo tee src/main/java/com/example/filecreator/config/WebConfig.java.production > /dev/null <<EOF
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://your-domain.com", "https://your-domain.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
EOF

# Rebuild backend
mvn clean package -DskipTests

# Restart backend
pm2 restart filecreator-backend
```

---

# Phase 8: Monitoring and Maintenance

## Step 8: Setup Monitoring

### 8.1 Create Monitoring Script
```bash
sudo tee /home/ubuntu/monitor.sh > /dev/null <<'EOF'
#!/bin/bash

# Check if backend is running
if ! pm2 list | grep -q "filecreator-backend.*online"; then
    echo "Backend down, restarting..."
    pm2 restart filecreator-backend
fi

# Check if nginx is running
if ! systemctl is-active --quiet nginx; then
    echo "Nginx down, restarting..."
    sudo systemctl restart nginx
fi

# Check disk space
df -h | awk '$5 > 80 {print "Disk usage high: " $5 " on " $1}'

# Check memory usage
free -h
EOF

chmod +x /home/ubuntu/monitor.sh

# Add to crontab for regular monitoring
(crontab -l 2>/dev/null; echo "*/5 * * * * /home/ubuntu/monitor.sh >> /var/log/monitor.log 2>&1") | crontab -
```

### 8.2 Log Rotation
```bash
sudo tee /etc/logrotate.d/filecreator > /dev/null <<EOF
/var/log/filecreator/*.log {
    daily
    missingok
    rotate 14
    compress
    delaycompress
    notifempty
    copytruncate
}
EOF
```

---

# Phase 9: Deployment Automation

## Step 9: Create Deployment Script

```bash
sudo tee /home/ubuntu/deploy.sh > /dev/null <<'EOF'
#!/bin/bash

set -e

echo "🚀 Starting deployment..."

cd /var/www/filecreator

# Pull latest changes
git pull origin master

# Build backend
echo "📦 Building backend..."
mvn clean package -DskipTests

# Build frontend
echo "📦 Building frontend..."
cd frontend
npm install
npm run build
cd ..

# Restart backend
echo "🔄 Restarting backend..."
pm2 restart filecreator-backend

# Restart nginx
echo "🔄 Restarting nginx..."
sudo systemctl restart nginx

echo "✅ Deployment completed successfully!"

# Health check
echo "🏥 Running health check..."
sleep 5
curl -f http://localhost/api/health || echo "❌ Health check failed"

pm2 status
EOF

chmod +x /home/ubuntu/deploy.sh
```

---

# Phase 10: Security Hardening

## Step 10: Security Configuration

### 10.1 Firewall Setup
```bash
# Setup UFW firewall
sudo ufw allow ssh
sudo ufw allow 'Nginx Full'
sudo ufw --force enable
sudo ufw status
```

### 10.2 Fail2Ban Setup
```bash
# Install fail2ban for SSH protection
sudo apt install fail2ban -y

sudo tee /etc/fail2ban/jail.local > /dev/null <<EOF
[DEFAULT]
bantime = 3600
findtime = 600
maxretry = 3

[sshd]
enabled = true
EOF

sudo systemctl enable fail2ban
sudo systemctl restart fail2ban
```

---

# Quick Reference Commands

## Management Commands
```bash
# Application Management
pm2 status                    # Check backend status
pm2 logs filecreator-backend  # View backend logs
pm2 restart filecreator-backend # Restart backend
sudo systemctl restart nginx  # Restart nginx

# Monitoring
tail -f /var/log/filecreator/application.log  # Backend logs
tail -f /var/log/nginx/access.log             # Nginx access logs
tail -f /var/log/nginx/error.log              # Nginx error logs

# Deployment
./deploy.sh                   # Deploy latest changes

# Health Checks
curl http://localhost/api/health              # Local health check
curl http://your-domain.com/api/health       # Public health check
```

## Troubleshooting
```bash
# Check if ports are listening
sudo netstat -tulpn | grep :80   # Nginx
sudo netstat -tulpn | grep :8080 # Backend

# Check service status
sudo systemctl status nginx
pm2 status

# View detailed logs
journalctl -u nginx -f
pm2 logs filecreator-backend --lines 100
```

---

# Cost Estimation

## AWS Costs (Monthly)
- **t2.micro (Free Tier)**: $0 for first 12 months
- **t3.small**: ~$15-20/month
- **Elastic IP**: $0 (if associated)
- **Data Transfer**: ~$0.09/GB (first 1GB free)
- **Storage**: ~$0.10/GB-month

## Domain & SSL
- **Domain**: $10-15/year
- **SSL**: Free (Let's Encrypt)

**Total Estimated Cost**: $15-25/month (after free tier)