import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { selectIsAuthenticated, selectRole } from '../store/slices/authSlice';

const ProtectedRoute = ({ children, allowedRoles }) => {
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const role = useSelector(selectRole);
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles && !allowedRoles.includes(role)) {
    return <Navigate to="/403" replace />;
  }

  return children;
};

export default ProtectedRoute;
