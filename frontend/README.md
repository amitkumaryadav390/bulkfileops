# FileCreator Frontend

A modern React frontend for the Excel/CSV to Word document generation application.

## Features

- **Drag & Drop File Upload** - Intuitive file upload with visual feedback
- **Processing Options** - Choose between individual or aggregated document generation
- **Real-time Feedback** - Progress indicators and error handling
- **Professional UI** - Clean, responsive design with modern styling
- **File Download** - Direct download of generated ZIP files

## Tech Stack

- **React 18** - Modern React with hooks
- **Webpack 5** - Module bundling and development server
- **Axios** - HTTP client for API communication
- **CSS3** - Professional styling with gradients and animations
- **Font Awesome** - Icon library
- **Google Fonts** - Inter font family

## Project Structure

```
frontend/
├── public/
│   └── index.html              # HTML template
├── src/
│   ├── components/
│   │   ├── Header.js           # Navigation header
│   │   ├── FileUpload.js       # File upload with drag-drop
│   │   ├── ProcessingOptions.js # Individual vs Aggregated options
│   │   ├── ResultDisplay.js    # Success/error results
│   │   └── Footer.js           # Footer component
│   ├── styles/
│   │   ├── index.css           # Base styles
│   │   └── App.css             # Component styles
│   ├── App.js                  # Main app component
│   └── index.js                # React entry point
├── package.json                # Dependencies and scripts
└── webpack.config.js           # Build configuration
```

## Quick Start

### Prerequisites
- Node.js 16+ and npm
- Spring Boot backend running on http://localhost:8080

### Installation

1. **Navigate to frontend directory:**
   ```powershell
   cd frontend
   ```

2. **Install dependencies:**
   ```powershell
   npm install
   ```

3. **Start development server:**
   ```powershell
   npm start
   ```

   The app will open at http://localhost:3000

### Production Build

```powershell
npm run build
```

Builds the app for production to the `dist/` folder.

## Usage

### 1. **Upload File**
   - Drag and drop Excel/CSV files or click to browse
   - Supported formats: .xlsx, .xls, .csv
   - Maximum file size: 10 MB

### 2. **Choose Processing Type**
   - **Individual Documents**: One Word document per Excel row
   - **Aggregated Documents**: Group by importer with combined data

### 3. **Generate & Download**
   - Click "Generate Documents" to process
   - Download the ZIP file containing generated Word documents
   - Extract to access individual documents

## API Integration

The frontend communicates with the Spring Boot backend:

- **Individual Generation**: `POST /api/generate-docs`
- **Aggregated Generation**: `POST /api/generate-aggregated-docs`

Both endpoints accept multipart/form-data with a file and return ZIP archives.

## Features in Detail

### File Upload Component
- **Drag & Drop**: Visual feedback for drag operations
- **File Validation**: Only accepts Excel/CSV files
- **Preview**: Shows file details before processing
- **Error Handling**: Clear messages for invalid files

### Processing Options
- **Radio Selection**: Visual choice between processing types
- **Information Cards**: Explains each option clearly
- **Loading States**: Shows processing progress
- **API Integration**: Handles both endpoints seamlessly

### Result Display
- **Success State**: Download button and file information
- **Error State**: Detailed error messages and troubleshooting
- **Reset Functionality**: Easy way to start over

## Development

### Available Scripts

```powershell
npm start       # Development server with hot reload
npm run build   # Production build
npm run dev     # Alternative development command
```

### Development Server
- Runs on http://localhost:3000
- Hot reload enabled
- Proxy API calls to backend (configure if needed)

## Deployment

### Static Hosting
After running `npm run build`, deploy the `dist/` folder to:
- Netlify
- Vercel
- GitHub Pages
- Any static hosting service

### Environment Configuration
For production, ensure the API base URL points to your deployed backend:

```javascript
// In ProcessingOptions.js, update the endpoint URLs:
const baseURL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
```

## Styling

The app uses a modern design with:
- **Color Scheme**: Purple gradients with professional grays
- **Typography**: Inter font family
- **Layout**: CSS Grid and Flexbox
- **Animations**: Smooth transitions and hover effects
- **Responsive**: Mobile-first design

### Key Design Elements
- Gradient backgrounds and buttons
- Card-based layout with shadows
- Professional color palette
- Consistent spacing and typography
- Interactive hover states

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Contributing

1. Follow the existing code structure
2. Maintain consistent styling patterns
3. Add comments for complex logic
4. Test on multiple browsers and devices

## Troubleshooting

### Common Issues

1. **Cannot connect to server**
   - Ensure Spring Boot backend is running on http://localhost:8080
   - Check CORS configuration in backend

2. **File upload fails**
   - Verify file format (Excel/CSV only)
   - Check file size (max 10 MB)
   - Ensure proper column headers in file

3. **Build fails**
   - Clear node_modules and reinstall: `rm -rf node_modules && npm install`
   - Check Node.js version compatibility

4. **Styling issues**
   - Clear browser cache
   - Check for CSS conflicts
   - Verify font loading from Google Fonts