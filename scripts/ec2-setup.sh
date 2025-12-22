#!/bin/bash

# 🚀 EC2 Initial Setup Script
# Run this script on a fresh Ubuntu EC2 instance

set -e

echo "🔧 Starting EC2 initial setup..."

# Update system
echo "📦 Updating system packages..."
sudo apt update && sudo apt upgrade -y

# Install Java 11
echo "☕ Installing Java 11..."
sudo apt install openjdk-11-jdk -y
echo "Java version: $(java -version 2>&1 | head -n 1)"

# Install Node.js 18
echo "📦 Installing Node.js 18..."
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
echo "Node.js version: $(node --version)"
echo "NPM version: $(npm --version)"

# Install Maven
echo "📦 Installing Maven..."
sudo apt install maven -y
echo "Maven version: $(mvn -version | head -n 1)"

# Install Nginx
echo "🌐 Installing Nginx..."
sudo apt install nginx -y
sudo systemctl enable nginx

# Install Git
echo "🔧 Installing Git..."
sudo apt install git -y

# Install PM2
echo "⚡ Installing PM2..."
sudo npm install -g pm2

# Install Certbot
echo "🔒 Installing Certbot..."
sudo apt install certbot python3-certbot-nginx -y

# Install additional utilities
echo "🔧 Installing utilities..."
sudo apt install htop curl wget unzip -y

# Create application directory
echo "📁 Creating application directory..."
sudo mkdir -p /var/www/filecreator
sudo chown ubuntu:ubuntu /var/www/filecreator

# Create log directory
echo "📁 Creating log directory..."
sudo mkdir -p /var/log/filecreator
sudo chown ubuntu:ubuntu /var/log/filecreator

# Setup basic firewall
echo "🔥 Setting up firewall..."
sudo ufw allow ssh
sudo ufw allow 'Nginx Full'
sudo ufw --force enable

# Install fail2ban for security
echo "🔒 Installing fail2ban..."
sudo apt install fail2ban -y

cat > /tmp/jail.local << EOF
[DEFAULT]
bantime = 3600
findtime = 600
maxretry = 3

[sshd]
enabled = true
EOF

sudo mv /tmp/jail.local /etc/fail2ban/jail.local
sudo systemctl enable fail2ban
sudo systemctl restart fail2ban

echo "✅ EC2 initial setup completed!"
echo ""
echo "Next steps:"
echo "1. Clone your repository to /var/www/filecreator"
echo "2. Run the application deployment script"
echo "3. Configure Nginx with your domain"
echo ""
echo "Installed versions:"
echo "- Java: $(java -version 2>&1 | head -n 1)"
echo "- Node.js: $(node --version)"
echo "- Maven: $(mvn -version | head -n 1 | cut -d' ' -f3)"
echo "- Nginx: $(nginx -v 2>&1)"
echo "- PM2: $(pm2 --version)"