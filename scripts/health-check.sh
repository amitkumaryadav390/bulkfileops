#!/bin/bash

# 📊 Monitoring and Maintenance Script
# Run this periodically to monitor application health

APP_DIR="/var/www/filecreator"
LOG_FILE="/var/log/monitor.log"

# Function to log with timestamp
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a $LOG_FILE
}

# Function to send notification (can be extended with email/slack)
notify() {
    log "ALERT: $1"
    # Add notification logic here (email, Slack, etc.)
}

log "🔍 Starting health check..."

# Check if backend is running
if ! pm2 list | grep -q "filecreator-backend.*online"; then
    notify "Backend is down, attempting restart..."
    pm2 restart filecreator-backend
    sleep 10
    if ! pm2 list | grep -q "filecreator-backend.*online"; then
        notify "Failed to restart backend!"
    else
        log "✅ Backend restarted successfully"
    fi
else
    log "✅ Backend is running"
fi

# Check if nginx is running
if ! systemctl is-active --quiet nginx; then
    notify "Nginx is down, attempting restart..."
    sudo systemctl restart nginx
    sleep 5
    if ! systemctl is-active --quiet nginx; then
        notify "Failed to restart Nginx!"
    else
        log "✅ Nginx restarted successfully"
    fi
else
    log "✅ Nginx is running"
fi

# Check API endpoint
if ! curl -f -s http://localhost:8080/api/health > /dev/null; then
    notify "Backend API is not responding!"
else
    log "✅ Backend API is responding"
fi

# Check frontend
if ! curl -f -s http://localhost/health > /dev/null; then
    notify "Frontend is not accessible!"
else
    log "✅ Frontend is accessible"
fi

# Check disk space
DISK_USAGE=$(df / | awk 'NR==2 {print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 80 ]; then
    notify "High disk usage: ${DISK_USAGE}%"
else
    log "✅ Disk usage: ${DISK_USAGE}%"
fi

# Check memory usage
MEMORY_USAGE=$(free | grep Mem | awk '{printf("%.0f", $3/$2 * 100)}')
if [ $MEMORY_USAGE -gt 85 ]; then
    notify "High memory usage: ${MEMORY_USAGE}%"
else
    log "✅ Memory usage: ${MEMORY_USAGE}%"
fi

# Check log file sizes
LOG_SIZE=$(du -sh /var/log/filecreator 2>/dev/null | cut -f1 || echo "0")
log "📊 Log directory size: $LOG_SIZE"

# Check for errors in logs (last 10 minutes)
ERROR_COUNT=$(journalctl --since "10 minutes ago" | grep -i error | wc -l)
if [ $ERROR_COUNT -gt 0 ]; then
    notify "Found $ERROR_COUNT errors in system logs in last 10 minutes"
fi

# Application-specific checks
if [ -f "$APP_DIR/target/filecreator-0.0.1-SNAPSHOT.jar" ]; then
    log "✅ Application JAR exists"
else
    notify "Application JAR not found!"
fi

if [ -d "$APP_DIR/frontend/dist" ]; then
    log "✅ Frontend build exists"
else
    notify "Frontend build directory not found!"
fi

# Check PM2 process memory
PM2_MEMORY=$(pm2 jlist | jq '.[0].monit.memory' 2>/dev/null || echo "0")
if [ $PM2_MEMORY -gt 536870912 ]; then  # 512MB in bytes
    notify "Backend using high memory: $(($PM2_MEMORY / 1024 / 1024))MB"
else
    log "✅ Backend memory usage: $(($PM2_MEMORY / 1024 / 1024))MB"
fi

log "✅ Health check completed"

# Cleanup old logs (keep last 7 days)
find /var/log/filecreator -name "*.log" -type f -mtime +7 -delete 2>/dev/null || true
find /var/log -name "monitor.log*" -type f -mtime +7 -delete 2>/dev/null || true