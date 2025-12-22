#!/bin/bash

# 🚀 Application Deployment Script for EC2
# Run this script after cloning your repository

set -e

DOMAIN=${1:-"your-ec2-ip"}  # Replace with your domain or EC2 IP
APP_DIR="/var/www/filecreator"

echo "🚀 Deploying FileCreator application..."
echo "Domain/IP: $DOMAIN"

cd $APP_DIR

# Check if we're in the right directory
if [[ ! -f "pom.xml" ]]; then
    echo "❌ pom.xml not found. Are you in the right directory?"
    exit 1
fi

# Create production application.properties
echo "⚙️ Creating production configuration..."
cat > src/main/resources/application-prod.properties << EOF
# Production Configuration
server.port=8080
server.address=127.0.0.1

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# CORS Configuration
cors.allowed-origins=http://${DOMAIN},https://${DOMAIN}

# Logging Configuration
logging.level.com.example.filecreator=INFO
logging.level.org.springframework.web=WARN
logging.file.name=/var/log/filecreator/application.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# Actuator endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
EOF

# Build backend
echo "📦 Building Spring Boot backend..."
mvn clean package -DskipTests

# Check if JAR was built successfully
JAR_FILE=$(find target -name "*.jar" -not -name "*sources.jar" | head -n 1)
if [[ -z "$JAR_FILE" ]]; then
    echo "❌ JAR file not found after build"
    exit 1
fi
echo "✅ Backend built successfully: $JAR_FILE"

# Update frontend for production
echo "📦 Building React frontend..."
cd frontend

# Check if package.json exists
if [[ ! -f "package.json" ]]; then
    echo "❌ package.json not found in frontend directory"
    exit 1
fi

# Install dependencies
npm install

# Create production environment file
cat > .env.production << EOF
REACT_APP_API_BASE_URL=
REACT_APP_ENV=production
EOF

# Build frontend
npm run build

# Check if build was successful
if [[ ! -d "dist" ]]; then
    echo "❌ Frontend build failed - dist directory not found"
    exit 1
fi
echo "✅ Frontend built successfully"

cd $APP_DIR

# Create PM2 ecosystem configuration
echo "⚡ Creating PM2 configuration..."
cat > ecosystem.config.js << EOF
module.exports = {
  apps: [{
    name: 'filecreator-backend',
    script: 'java',
    args: [
      '-jar',
      '-Dspring.profiles.active=prod',
      '-Xmx512m',
      '$JAR_FILE'
    ],
    cwd: '$APP_DIR',
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

# Start backend with PM2
echo "🚀 Starting backend with PM2..."
pm2 delete filecreator-backend 2>/dev/null || true  # Delete if exists
pm2 start ecosystem.config.js
pm2 save

# Create Nginx configuration
echo "🌐 Creating Nginx configuration..."
sudo tee /etc/nginx/sites-available/filecreator > /dev/null << EOF
server {
    listen 80;
    server_name $DOMAIN;
    
    # Security headers
    add_header X-Content-Type-Options nosniff;
    add_header X-Frame-Options DENY;
    add_header X-XSS-Protection "1; mode=block";
    add_header Referrer-Policy strict-origin-when-cross-origin;
    
    # Enable gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 10240;
    gzip_proxied expired no-cache no-store private must-revalidate;
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/javascript;
    
    # Serve React frontend (static files)
    location / {
        root $APP_DIR/frontend/dist;
        index index.html;
        try_files \$uri \$uri/ /index.html;
        
        # Cache static assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
        
        # Prevent caching of index.html
        location = /index.html {
            expires -1;
            add_header Cache-Control "no-cache, no-store, must-revalidate";
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
        
        # Add CORS headers for API
        add_header Access-Control-Allow-Origin "*" always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "Content-Type, Authorization" always;
        
        # Handle preflight requests
        if (\$request_method = 'OPTIONS') {
            add_header Access-Control-Allow-Origin "*";
            add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
            add_header Access-Control-Allow-Headers "Content-Type, Authorization";
            add_header Access-Control-Max-Age 86400;
            add_header Content-Length 0;
            add_header Content-Type text/plain;
            return 204;
        }
    }
    
    # Health check endpoint
    location /health {
        access_log off;
        return 200 "healthy\\n";
        add_header Content-Type text/plain;
    }
    
    # Logs
    access_log /var/log/nginx/filecreator-access.log;
    error_log /var/log/nginx/filecreator-error.log;
}
EOF

# Enable the site
sudo rm -f /etc/nginx/sites-enabled/default
sudo ln -sf /etc/nginx/sites-available/filecreator /etc/nginx/sites-enabled/

# Test nginx configuration
echo "🔍 Testing Nginx configuration..."
sudo nginx -t

# Restart Nginx
echo "🔄 Restarting Nginx..."
sudo systemctl restart nginx

# Wait for services to start
echo "⏳ Waiting for services to start..."
sleep 10

# Health checks
echo "🏥 Running health checks..."

# Check if PM2 is running
if pm2 list | grep -q "filecreator-backend.*online"; then
    echo "✅ Backend is running"
else
    echo "❌ Backend is not running"
    pm2 logs filecreator-backend --lines 20
fi

# Check if Nginx is running
if sudo systemctl is-active --quiet nginx; then
    echo "✅ Nginx is running"
else
    echo "❌ Nginx is not running"
    sudo systemctl status nginx
fi

# Test endpoints
echo "🔍 Testing endpoints..."
sleep 5

# Test health endpoint
if curl -f http://localhost/health > /dev/null 2>&1; then
    echo "✅ Health endpoint is working"
else
    echo "❌ Health endpoint is not responding"
fi

# Test backend health
if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "✅ Backend health endpoint is working"
else
    echo "❌ Backend health endpoint is not responding"
fi

echo ""
echo "🎉 Deployment completed!"
echo ""
echo "📊 Application Status:"
pm2 status
echo ""
echo "🌐 Access your application:"
echo "  Frontend: http://$DOMAIN"
echo "  Health:   http://$DOMAIN/health"
echo "  API:      http://$DOMAIN/api/health"
echo ""
echo "📝 Useful commands:"
echo "  View backend logs: pm2 logs filecreator-backend"
echo "  View nginx logs:   sudo tail -f /var/log/nginx/filecreator-error.log"
echo "  Restart backend:   pm2 restart filecreator-backend"
echo "  Restart nginx:     sudo systemctl restart nginx"
echo ""

if [[ "$DOMAIN" != *"."* ]]; then
    echo "💡 Tips:"
    echo "  1. Replace 'your-ec2-ip' with your actual EC2 public IP or domain"
    echo "  2. For production, consider setting up SSL with: sudo certbot --nginx -d $DOMAIN"
    echo "  3. Update your domain's DNS to point to this EC2 instance"
fi