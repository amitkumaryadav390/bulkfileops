#!/bin/bash

# 🔒 SSL Setup Script for Production
# Run this script to setup SSL certificate with Let's Encrypt

set -e

DOMAIN=$1
EMAIL=$2

if [[ -z "$DOMAIN" ]] || [[ -z "$EMAIL" ]]; then
    echo "Usage: ./ssl-setup.sh <domain> <email>"
    echo "Example: ./ssl-setup.sh myapp.example.com admin@example.com"
    exit 1
fi

echo "🔒 Setting up SSL for $DOMAIN..."

# Check if domain resolves to this server
echo "🔍 Checking DNS resolution..."
DOMAIN_IP=$(dig +short $DOMAIN)
SERVER_IP=$(curl -s http://checkip.amazonaws.com/)

if [[ "$DOMAIN_IP" != "$SERVER_IP" ]]; then
    echo "⚠️  Warning: Domain $DOMAIN resolves to $DOMAIN_IP but server IP is $SERVER_IP"
    echo "Please ensure your DNS is configured correctly before proceeding."
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Test if the site is accessible
echo "🔍 Testing HTTP access..."
if ! curl -f http://$DOMAIN/health > /dev/null 2>&1; then
    echo "❌ Cannot access http://$DOMAIN/health"
    echo "Please ensure your application is running and accessible via HTTP first"
    exit 1
fi

echo "✅ HTTP access confirmed"

# Obtain SSL certificate
echo "📜 Obtaining SSL certificate..."
sudo certbot --nginx \
    --non-interactive \
    --agree-tos \
    --email $EMAIL \
    --domains $DOMAIN \
    --redirect

# Test SSL configuration
echo "🔍 Testing SSL configuration..."
if openssl s_client -connect $DOMAIN:443 -servername $DOMAIN < /dev/null > /dev/null 2>&1; then
    echo "✅ SSL certificate is working"
else
    echo "❌ SSL certificate test failed"
    exit 1
fi

# Setup auto-renewal
echo "⚙️ Setting up auto-renewal..."
(crontab -l 2>/dev/null; echo "0 12 * * * /usr/bin/certbot renew --quiet") | crontab -

# Test auto-renewal
echo "🔍 Testing auto-renewal..."
sudo certbot renew --dry-run

echo ""
echo "🎉 SSL setup completed successfully!"
echo ""
echo "🔒 Your application is now available at:"
echo "  HTTPS: https://$DOMAIN"
echo "  HTTP:  http://$DOMAIN (redirects to HTTPS)"
echo ""
echo "📅 Certificate auto-renewal is configured to run daily at 12:00 PM"
echo ""
echo "🔍 Certificate information:"
sudo certbot certificates