# 📋 AWS EC2 Deployment Checklist

## Pre-Deployment
- [ ] AWS Account setup with billing configured
- [ ] Domain name registered (optional but recommended)
- [ ] SSH Key Pair created in AWS
- [ ] Code pushed to GitHub repository

## EC2 Instance Setup
- [ ] Launch Ubuntu 22.04 LTS EC2 instance
- [ ] Configure Security Groups (ports 22, 80, 443)
- [ ] Associate Elastic IP (optional but recommended)
- [ ] Connect via SSH successfully

## Environment Setup
- [ ] Run `ec2-setup.sh` script
- [ ] Verify Java 11 installation
- [ ] Verify Node.js 18+ installation
- [ ] Verify Maven installation
- [ ] Verify Nginx installation

## Code Deployment
- [ ] Clone repository to `/var/www/filecreator`
- [ ] Run `deploy-app.sh your-domain.com` script
- [ ] Verify backend build successful
- [ ] Verify frontend build successful
- [ ] Check PM2 process running
- [ ] Check Nginx configuration

## Testing
- [ ] Test health endpoint: `curl http://your-domain/health`
- [ ] Test API endpoint: `curl http://your-domain/api/health`
- [ ] Test file upload functionality
- [ ] Test document generation
- [ ] Test both individual and aggregated processing

## SSL Setup (Production)
- [ ] Configure DNS to point to EC2 instance
- [ ] Run `ssl-setup.sh your-domain.com your-email@domain.com`
- [ ] Verify HTTPS access
- [ ] Test certificate auto-renewal

## Monitoring Setup
- [ ] Setup cron job for health checks
- [ ] Configure log rotation
- [ ] Setup disk space monitoring
- [ ] Test PM2 auto-restart functionality

## Security
- [ ] Configure UFW firewall
- [ ] Setup fail2ban for SSH protection
- [ ] Regular security updates scheduled
- [ ] Application logs monitoring

## Backup Strategy
- [ ] Database backup (if applicable)
- [ ] Application files backup
- [ ] Configuration files backup
- [ ] Regular snapshot schedule

## Documentation
- [ ] Update README with production URLs
- [ ] Document deployment process
- [ ] Create runbook for maintenance
- [ ] Share access credentials securely

## Post-Deployment
- [ ] Monitor application for 24 hours
- [ ] Check error logs
- [ ] Verify performance metrics
- [ ] Test fail-over scenarios
- [ ] Update team on go-live status

---

## Quick Commands Reference

### Application Management
```bash
# Check application status
pm2 status
sudo systemctl status nginx

# Restart services
pm2 restart filecreator-backend
sudo systemctl restart nginx

# View logs
pm2 logs filecreator-backend
sudo tail -f /var/log/nginx/filecreator-error.log
tail -f /var/log/filecreator/application.log

# Deploy updates
cd /var/www/filecreator
git pull origin master
./scripts/deploy-app.sh your-domain.com
```

### Health Checks
```bash
# Quick health check
curl http://localhost/health
curl http://localhost/api/health

# Full health check
./scripts/health-check.sh
```

### Monitoring
```bash
# System resources
htop
df -h
free -h

# Application metrics
pm2 monit
sudo netstat -tulpn | grep :80
sudo netstat -tulpn | grep :8080
```

---

## Troubleshooting

### Backend Issues
1. Check PM2 logs: `pm2 logs filecreator-backend`
2. Check Java process: `ps aux | grep java`
3. Check port binding: `sudo netstat -tulpn | grep :8080`
4. Restart backend: `pm2 restart filecreator-backend`

### Frontend Issues
1. Check Nginx error logs: `sudo tail -f /var/log/nginx/error.log`
2. Check file permissions: `ls -la /var/www/filecreator/frontend/dist`
3. Test Nginx config: `sudo nginx -t`
4. Restart Nginx: `sudo systemctl restart nginx`

### SSL Issues
1. Check certificate: `sudo certbot certificates`
2. Test SSL: `openssl s_client -connect your-domain.com:443`
3. Renew certificate: `sudo certbot renew`
4. Check Nginx SSL config: `sudo nginx -t`

### Performance Issues
1. Monitor resources: `htop`, `iotop`
2. Check application logs for errors
3. Monitor PM2 metrics: `pm2 monit`
4. Check disk space: `df -h`