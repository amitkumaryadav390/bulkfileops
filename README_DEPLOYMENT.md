# 🚀 Complete AWS EC2 Deployment Plan for FileCreator Application

## 📋 Executive Summary

This deployment plan provides step-by-step instructions to deploy your **FileCreator** application (Spring Boot backend + React frontend) on AWS EC2 with professional production setup including:

- ✅ **Automated deployment scripts**
- ✅ **SSL/HTTPS configuration**
- ✅ **Reverse proxy with Nginx**
- ✅ **Process management with PM2**
- ✅ **Health monitoring**
- ✅ **Security hardening**
- ✅ **Cost optimization**

## 🏗️ Architecture Overview

```
Internet → AWS EC2 → Nginx (Port 80/443) → {
    /api/* → Spring Boot Backend (Port 8080)
    /*     → React Frontend (Static Files)
}
```

## 💰 Cost Estimate

| Resource | Cost (Monthly) |
|----------|----------------|
| **t2.micro** (Free Tier) | $0 (first 12 months) |
| **t3.small** (Production) | $15-20 |
| **Elastic IP** | $0 (if associated) |
| **Domain** | $10-15/year |
| **SSL Certificate** | Free (Let's Encrypt) |
| **Total** | **$15-25/month** |

---

# 🚀 Quick Start (30 Minutes)

## Step 1: Launch EC2 Instance (5 min)

1. **Go to AWS Console** → EC2 → Launch Instance
2. **Select**: Ubuntu Server 22.04 LTS
3. **Instance Type**: t2.micro (free tier) or t3.small
4. **Key Pair**: Create new or select existing
5. **Security Groups**: Allow ports 22, 80, 443
6. **Launch** and note the **Public IP**

## Step 2: Connect and Setup Environment (10 min)

```bash
# Connect to your EC2 instance
ssh -i "your-key.pem" ubuntu@YOUR_EC2_IP

# Clone your repository
git clone https://github.com/amitkumaryadav390/quizuploader.git /tmp/filecreator
cd /tmp/filecreator

# Make scripts executable
chmod +x scripts/*.sh

# Run initial setup
sudo ./scripts/ec2-setup.sh
```

## Step 3: Deploy Application (10 min)

```bash
# Copy application to deployment directory
sudo cp -r /tmp/filecreator/* /var/www/filecreator/
cd /var/www/filecreator

# Deploy application (replace YOUR_EC2_IP with actual IP)
./scripts/deploy-app.sh YOUR_EC2_IP
```

## Step 4: Verify Deployment (5 min)

```bash
# Check services status
pm2 status
sudo systemctl status nginx

# Test endpoints
curl http://YOUR_EC2_IP/health
curl http://YOUR_EC2_IP/api/health

# Open in browser
echo "🌐 Your application is live at: http://YOUR_EC2_IP"
```

---

# 📚 Detailed Deployment Guide

## Phase 1: AWS EC2 Instance Setup

### 1.1 Launch Instance

| Setting | Value |
|---------|-------|
| **AMI** | Ubuntu Server 22.04 LTS |
| **Instance Type** | t2.micro (free) or t3.small |
| **Storage** | 8-20 GB |
| **Key Pair** | Create/Select your SSH key |

### 1.2 Security Group Configuration

```
Type        Port    Source      Description
SSH         22      Your IP     SSH access
HTTP        80      0.0.0.0/0   Web traffic
HTTPS       443     0.0.0.0/0   Secure web traffic
```

### 1.3 Optional: Elastic IP

```bash
# In AWS Console → EC2 → Elastic IPs
# 1. Allocate new Elastic IP
# 2. Associate with your instance
# Benefits: Static IP that doesn't change on restart
```

## Phase 2: Environment Setup

### 2.1 Connect to Instance

```bash
# Replace with your actual key file and IP
ssh -i "your-key-file.pem" ubuntu@YOUR_EC2_IP

# Update system
sudo apt update && sudo apt upgrade -y
```

### 2.2 Run Automated Setup

```bash
# Clone repository
git clone https://github.com/amitkumaryadav390/quizuploader.git
cd quizuploader

# Make scripts executable
chmod +x scripts/*.sh

# Run setup script (installs Java, Node.js, Maven, Nginx, PM2, etc.)
sudo ./scripts/ec2-setup.sh

# Copy to deployment location
sudo cp -r . /var/www/filecreator/
sudo chown -R ubuntu:ubuntu /var/www/filecreator
```

## Phase 3: Application Deployment

### 3.1 Deploy with Script

```bash
cd /var/www/filecreator

# Deploy application (use your EC2 public IP or domain)
./scripts/deploy-app.sh YOUR_EC2_IP_OR_DOMAIN
```

**What this script does:**
- ✅ Builds Spring Boot backend
- ✅ Builds React frontend for production
- ✅ Configures PM2 process management
- ✅ Sets up Nginx reverse proxy
- ✅ Starts all services
- ✅ Runs health checks

### 3.2 Verify Deployment

```bash
# Check PM2 processes
pm2 status

# Check Nginx status
sudo systemctl status nginx

# Test health endpoints
curl http://localhost/health          # Should return "healthy"
curl http://localhost/api/health      # Should return backend health info

# Check logs if needed
pm2 logs filecreator-backend
sudo tail -f /var/log/nginx/error.log
```

## Phase 4: Domain & SSL Setup (Production)

### 4.1 Domain Configuration

```bash
# 1. Point your domain to EC2 IP in your DNS provider
# 2. Wait for DNS propagation (5-30 minutes)
# 3. Test: ping your-domain.com

# Update Nginx for your domain
sudo sed -i 's/your-ec2-ip/your-domain.com/g' /etc/nginx/sites-available/filecreator
sudo systemctl restart nginx
```

### 4.2 SSL Certificate

```bash
# Setup SSL with Let's Encrypt
./scripts/ssl-setup.sh your-domain.com your-email@domain.com

# This will:
# - Obtain SSL certificate
# - Configure Nginx for HTTPS
# - Setup auto-renewal
# - Test certificate
```

## Phase 5: Monitoring & Maintenance

### 5.1 Setup Health Monitoring

```bash
# Make health check script executable
chmod +x scripts/health-check.sh

# Test the health check
./scripts/health-check.sh

# Setup cron job for monitoring (every 5 minutes)
(crontab -l 2>/dev/null; echo "*/5 * * * * /var/www/filecreator/scripts/health-check.sh") | crontab -

# Check cron job
crontab -l
```

### 5.2 Log Management

```bash
# Setup log rotation
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

# 🛠️ Operations & Maintenance

## Daily Operations

### Start/Stop/Restart Services

```bash
# Backend operations
pm2 start filecreator-backend     # Start
pm2 stop filecreator-backend      # Stop
pm2 restart filecreator-backend   # Restart
pm2 delete filecreator-backend    # Remove

# Nginx operations
sudo systemctl start nginx        # Start
sudo systemctl stop nginx         # Stop
sudo systemctl restart nginx      # Restart
```

### View Logs

```bash
# Backend logs
pm2 logs filecreator-backend           # Live logs
pm2 logs filecreator-backend --lines 100  # Last 100 lines

# Nginx logs
sudo tail -f /var/log/nginx/access.log     # Access logs
sudo tail -f /var/log/nginx/error.log      # Error logs

# Application logs
tail -f /var/log/filecreator/application.log
```

### Monitor Resources

```bash
# System monitoring
htop                              # Interactive process viewer
df -h                            # Disk usage
free -h                          # Memory usage
pm2 monit                        # PM2 monitoring dashboard

# Network monitoring
sudo netstat -tulpn | grep :80   # Check port 80
sudo netstat -tulpn | grep :8080 # Check backend port
```

## Updates & Deployments

### Deploy New Version

```bash
cd /var/www/filecreator

# Pull latest code
git pull origin master

# Deploy
./scripts/deploy-app.sh your-domain.com

# Or manual deployment:
mvn clean package -DskipTests     # Build backend
cd frontend && npm run build && cd ..  # Build frontend
pm2 restart filecreator-backend   # Restart backend
sudo systemctl restart nginx      # Restart nginx
```

### Rollback Strategy

```bash
# If deployment fails, rollback:
git log --oneline -5              # See recent commits
git checkout PREVIOUS_COMMIT_HASH # Rollback code
./scripts/deploy-app.sh your-domain.com  # Redeploy
```

---

# 🔧 Troubleshooting Guide

## Common Issues & Solutions

### 1. Backend Not Starting

**Symptoms:** `pm2 status` shows backend as "errored" or "stopped"

```bash
# Check logs
pm2 logs filecreator-backend

# Common fixes:
java -version                     # Ensure Java is installed
ls -la target/*.jar              # Ensure JAR file exists
pm2 restart filecreator-backend  # Restart process

# Manual start for debugging
cd /var/www/filecreator
java -jar -Dspring.profiles.active=prod target/*.jar
```

### 2. Frontend Not Loading

**Symptoms:** Browser shows "502 Bad Gateway" or nginx error

```bash
# Check nginx status
sudo systemctl status nginx

# Check nginx configuration
sudo nginx -t

# Check file permissions
ls -la /var/www/filecreator/frontend/dist/

# Common fixes:
sudo systemctl restart nginx
sudo chown -R ubuntu:ubuntu /var/www/filecreator
```

### 3. API Calls Failing

**Symptoms:** Frontend loads but file upload fails

```bash
# Check if backend is responding
curl http://localhost:8080/api/health

# Check CORS configuration
# Edit src/main/java/.../config/WebConfig.java
# Ensure your domain is in allowedOrigins

# Check nginx proxy configuration
sudo cat /etc/nginx/sites-available/filecreator

# Check backend logs for errors
pm2 logs filecreator-backend
```

### 4. SSL Certificate Issues

**Symptoms:** Browser shows "Not Secure" or certificate errors

```bash
# Check certificate status
sudo certbot certificates

# Test SSL manually
openssl s_client -connect your-domain.com:443

# Renew certificate
sudo certbot renew

# Check nginx SSL configuration
sudo nginx -t
```

### 5. High Resource Usage

**Symptoms:** Application slow or EC2 instance unresponsive

```bash
# Check resources
htop
df -h
free -h

# Check backend memory
pm2 monit

# Restart if needed
pm2 restart filecreator-backend

# Consider upgrading instance type if consistently high
```

---

# 📊 Monitoring & Alerts

## Application Health Checks

### Manual Health Checks

```bash
# Quick health check
curl http://your-domain.com/health
curl http://your-domain.com/api/health

# Full system check
./scripts/health-check.sh
```

### Automated Monitoring Setup

```bash
# The health-check.sh script monitors:
# - Backend process status
# - Nginx status
# - API endpoint availability
# - Disk usage
# - Memory usage
# - Error logs

# View monitoring logs
tail -f /var/log/monitor.log
```

## Performance Monitoring

### Application Metrics

```bash
# Backend performance
pm2 monit                         # Real-time monitoring
pm2 list                         # Process list with stats

# Web server metrics
sudo tail -f /var/log/nginx/access.log | grep -v "GET /health"
```

### System Metrics

```bash
# System performance
iotop                            # I/O usage
netstat -i                       # Network interface stats
ss -tuln                         # Network connections
```

---

# 🔒 Security Best Practices

## Implemented Security Measures

✅ **UFW Firewall** - Restricts access to necessary ports only  
✅ **Fail2ban** - Protects against SSH brute force attacks  
✅ **SSL/TLS** - Encrypts all web traffic  
✅ **Security Headers** - X-Frame-Options, X-XSS-Protection, etc.  
✅ **Process Isolation** - Backend runs as non-root user  
✅ **Input Validation** - File upload restrictions  

## Additional Security Hardening

### 1. SSH Hardening

```bash
# Edit SSH configuration
sudo nano /etc/ssh/sshd_config

# Recommended settings:
# PermitRootLogin no
# PasswordAuthentication no
# PubkeyAuthentication yes
# Port 2222  # Change from default 22

sudo systemctl restart ssh
```

### 2. Automatic Updates

```bash
# Install unattended upgrades
sudo apt install unattended-upgrades

# Configure automatic security updates
sudo dpkg-reconfigure unattended-upgrades
```

### 3. File Upload Security

```bash
# Already implemented in application:
# - File size limits (10MB)
# - File type validation
# - Upload path restrictions
```

---

# 📝 Backup Strategy

## Application Backup

### 1. Code Backup
```bash
# Already handled by Git repository
# Ensure regular commits and pushes
```

### 2. Configuration Backup
```bash
# Backup important configuration files
sudo tar -czf backup-config-$(date +%Y%m%d).tar.gz \
  /etc/nginx/sites-available/filecreator \
  /var/www/filecreator/ecosystem.config.js \
  /var/www/filecreator/src/main/resources/application-prod.properties
```

### 3. Log Backup
```bash
# Backup application logs
sudo tar -czf backup-logs-$(date +%Y%m%d).tar.gz \
  /var/log/filecreator/ \
  /var/log/nginx/filecreator-*.log
```

## EC2 Instance Backup

### 1. EBS Snapshots
```bash
# In AWS Console:
# EC2 → Snapshots → Create Snapshot
# Select your instance's volume
# Create snapshot with description
```

### 2. AMI Creation
```bash
# In AWS Console:
# EC2 → Instances → Right-click instance → Create Image
# This creates a complete backup of your configured instance
```

---

# 🚀 Scaling & Optimization

## Vertical Scaling (Upgrade Instance)

### When to Scale Up:
- CPU usage consistently > 80%
- Memory usage > 85%
- Response times increasing

### How to Scale:
```bash
# 1. Stop instance
# 2. Change instance type in AWS Console
# 3. Start instance
# 4. Services auto-start with PM2
```

## Horizontal Scaling (Multiple Instances)

### Load Balancer Setup:
```bash
# For high traffic, consider:
# 1. Application Load Balancer (ALB)
# 2. Multiple EC2 instances
# 3. RDS for shared database
# 4. S3 for file storage
```

## Performance Optimization

### 1. Frontend Optimization
```bash
# Already implemented:
# - Gzip compression
# - Static asset caching
# - CDN-ready configuration
```

### 2. Backend Optimization
```bash
# JVM tuning in ecosystem.config.js:
# -Xmx512m    # Maximum heap size
# -Xms256m    # Initial heap size
# -XX:+UseG1GC # Garbage collector
```

---

# 📞 Support & Troubleshooting

## Emergency Recovery

### If Application is Down:
```bash
# 1. Connect to EC2
ssh -i your-key.pem ubuntu@your-ec2-ip

# 2. Quick restart
pm2 restart all
sudo systemctl restart nginx

# 3. Check logs
pm2 logs filecreator-backend
sudo tail -f /var/log/nginx/error.log
```

### If EC2 is Unresponsive:
```bash
# 1. AWS Console → EC2 → Instances
# 2. Select instance → Instance State → Reboot
# 3. Services will auto-start due to PM2 startup script
```

## Getting Help

### Log Collection for Support:
```bash
# Collect all relevant logs
sudo tar -czf debug-logs-$(date +%Y%m%d-%H%M).tar.gz \
  /var/log/filecreator/ \
  /var/log/nginx/ \
  /var/log/syslog \
  ~/.pm2/logs/
```

### System Information:
```bash
# Collect system info
{
  echo "=== System Info ==="
  uname -a
  echo "=== Java Version ==="
  java -version
  echo "=== Node Version ==="
  node --version
  echo "=== PM2 Status ==="
  pm2 status
  echo "=== Nginx Status ==="
  sudo systemctl status nginx
  echo "=== Disk Usage ==="
  df -h
  echo "=== Memory Usage ==="
  free -h
} > system-info.txt
```

---

# 🎯 Success Checklist

After deployment, verify these items:

## ✅ Basic Functionality
- [ ] Application loads at your domain/IP
- [ ] Health endpoint responds: `/health`
- [ ] API endpoint responds: `/api/health`
- [ ] File upload works
- [ ] Document generation works
- [ ] Aggregated processing works

## ✅ Performance
- [ ] Page loads in < 3 seconds
- [ ] File processing completes successfully
- [ ] No memory leaks (monitor for 24 hours)
- [ ] CPU usage < 80% under normal load

## ✅ Security
- [ ] HTTPS enabled (if using domain)
- [ ] No unnecessary ports open
- [ ] Fail2ban protecting SSH
- [ ] Application runs as non-root user

## ✅ Monitoring
- [ ] Health checks running automatically
- [ ] Log rotation configured
- [ ] PM2 auto-restart working
- [ ] SSL auto-renewal scheduled

## ✅ Documentation
- [ ] Deployment steps documented
- [ ] Access credentials secured
- [ ] Team notified of go-live
- [ ] Backup strategy in place

---

# 🏆 Congratulations!

Your **FileCreator** application is now successfully deployed on AWS EC2 with:

- 🔒 **Production-grade security**
- 📊 **Comprehensive monitoring** 
- 🚀 **Automated deployments**
- 💰 **Cost-optimized setup**
- 📈 **Scalability ready**

**Your application is now live and ready for users! 🎉**

---

*For any issues or questions, refer to the troubleshooting section or collect logs as described in the support section.*