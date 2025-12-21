import React, { useRef, useState } from 'react';

const FileUpload = ({ onFileUpload, uploadedFile, isProcessing }) => {
  const fileInputRef = useRef(null);
  const [dragActive, setDragActive] = useState(false);

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFile(e.dataTransfer.files[0]);
    }
  };

  const handleChange = (e) => {
    e.preventDefault();
    if (e.target.files && e.target.files[0]) {
      handleFile(e.target.files[0]);
    }
  };

  const handleFile = (file) => {
    const allowedTypes = [
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
      'application/vnd.ms-excel', // .xls
      'text/csv' // .csv
    ];
    
    if (allowedTypes.includes(file.type) || file.name.endsWith('.csv') || file.name.endsWith('.xlsx') || file.name.endsWith('.xls')) {
      onFileUpload(file);
    } else {
      alert('Please upload only Excel (.xlsx, .xls) or CSV (.csv) files');
    }
  };

  const openFileSelector = () => {
    if (!isProcessing) {
      fileInputRef.current?.click();
    }
  };

  const removeFile = () => {
    if (!isProcessing) {
      onFileUpload(null);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  return (
    <div className="file-upload">
      {!uploadedFile ? (
        <div
          className={`upload-area ${dragActive ? 'drag-active' : ''} ${isProcessing ? 'disabled' : ''}`}
          onDragEnter={handleDrag}
          onDragLeave={handleDrag}
          onDragOver={handleDrag}
          onDrop={handleDrop}
          onClick={openFileSelector}
        >
          <input
            ref={fileInputRef}
            type="file"
            className="file-input"
            multiple={false}
            onChange={handleChange}
            accept=".csv,.xlsx,.xls,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel,text/csv"
            disabled={isProcessing}
          />
          
          <div className="upload-content">
            <i className="fas fa-cloud-upload-alt upload-icon"></i>
            <h4>Drop your file here or click to browse</h4>
            <p>Supports Excel (.xlsx, .xls) and CSV (.csv) files</p>
            <div className="upload-button">
              <i className="fas fa-plus"></i>
              Choose File
            </div>
          </div>
        </div>
      ) : (
        <div className="file-preview">
          <div className="file-info">
            <div className="file-icon">
              <i className={`fas ${uploadedFile.name.endsWith('.csv') ? 'fa-file-csv' : 'fa-file-excel'}`}></i>
            </div>
            <div className="file-details">
              <h4 className="file-name">{uploadedFile.name}</h4>
              <p className="file-size">{formatFileSize(uploadedFile.size)}</p>
              <p className="file-type">{uploadedFile.type || 'Unknown type'}</p>
            </div>
          </div>
          
          {!isProcessing && (
            <button 
              className="remove-button"
              onClick={removeFile}
              type="button"
            >
              <i className="fas fa-times"></i>
            </button>
          )}
          
          {isProcessing && (
            <div className="processing-indicator">
              <i className="fas fa-spinner fa-spin"></i>
            </div>
          )}
        </div>
      )}
      
      <div className="upload-help">
        <h5>File Requirements:</h5>
        <ul>
          <li>Excel files (.xlsx, .xls) or CSV files (.csv)</li>
          <li>Maximum file size: 10 MB</li>
          <li>Should contain columns matching the template placeholders</li>
        </ul>
      </div>
    </div>
  );
};

export default FileUpload;