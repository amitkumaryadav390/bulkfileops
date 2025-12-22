# 🔧 Troubleshooting Guide: Frontend-Backend Connection

## Issue: "Cannot connect to server"

### Step-by-Step Fix:

## **Step 1: Start the Backend**
```powershell
# Navigate to project root
cd c:\Users\komal\Desktop\amit\project\filecreator

# Start Spring Boot (choose one method):

# Method A: Using Maven (recommended)
mvn spring-boot:run

# Method B: Using Java directly (if jar exists)
java -jar target\filecreator-0.0.1-SNAPSHOT.jar

# Method C: If you have IDE, run FileCreatorApplication.java
```

## **Step 2: Test Backend Connection**
```powershell
# Run the test script
.\test-backend.ps1

# OR manually test health endpoint
curl http://localhost:8080/api/health
```

Expected response: `"Server is running successfully!"`

## **Step 3: Check for Common Issues**

### **A. Port Already in Use**
If you see "Port 8080 is already in use":
```powershell
# Find what's using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F

# Or use a different port by adding to application.properties:
# server.port=8081
```

### **B. Maven Not Available**
If `mvn` command not found:
```powershell
# Check if Maven is installed
mvn -version

# If not installed, download from: https://maven.apache.org/download.cgi
# Or use your IDE to run the Spring Boot application directly
```

### **C. Java Version Issues**
```powershell
# Check Java version (needs 11+)
java -version

# If wrong version, set JAVA_HOME or install correct version
```

## **Step 4: Verify Frontend Configuration**
The frontend is configured to call:
- `http://localhost:8080/api/generate-docs`
- `http://localhost:8080/api/generate-aggregated-docs`

If you change the backend port, update these URLs in:
`frontend/src/components/ProcessingOptions.js`

## **Step 5: Test Full Workflow**

1. **Start Backend:**
   ```powershell
   mvn spring-boot:run
   ```
   Wait for: "Started FileCreatorApplication in X.XXX seconds"

2. **Start Frontend:**
   ```powershell
   cd frontend
   npm start
   ```
   Opens browser at http://localhost:3000

3. **Test Upload:**
   - Upload the provided `Rough_csv.csv` file
   - Should work if backend is running

## **Common Error Messages:**

### "ERR_NETWORK"
- Backend is not running
- Wrong port
- Firewall blocking connection

### "404 Not Found"
- Backend running but endpoints not mapped correctly
- Check the @RequestMapping annotations

### "CORS Error"
- Fixed with the WebConfig.java we added
- Make sure @CrossOrigin annotation is present

### "400 Bad Request"
- File format issue
- File too large (>10MB)
- Missing template file

## **Quick Verification Commands:**

```powershell
# 1. Is backend running?
curl http://localhost:8080/api/health

# 2. Can frontend reach backend?
# (From browser console at localhost:3000)
fetch('http://localhost:8080/api/health').then(r => r.text()).then(console.log)

# 3. Are both servers running?
netstat -ano | findstr :8080
netstat -ano | findstr :3000
```

## **If Still Not Working:**

1. **Check Windows Firewall**
2. **Restart both applications**
3. **Try different browsers**
4. **Check antivirus blocking connections**
5. **Use browser developer tools to see exact error**

## **Success Indicators:**
- ✅ Backend logs show: "Started FileCreatorApplication"
- ✅ Health check returns: "Server is running successfully!"
- ✅ Frontend shows file upload interface
- ✅ No CORS errors in browser console
- ✅ File upload processes successfully