import React, { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Provider, useDispatch } from 'react-redux';
import { ThemeProvider, createTheme, CssBaseline } from '@mui/material';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import store from './store/store';
import { restoreSession } from './store/slices/authSlice';
import ProtectedRoute from './components/common/ProtectedRoute';

// Pages — Auth
import LoginPage from './pages/auth/LoginPage';

// Pages — Student
import StudentDashboard from './pages/student/StudentDashboard';
import StudentResults from './pages/student/StudentResults';
import StudentContent from './pages/student/StudentContent';
import OnlineTest from './pages/student/OnlineTest';

// Pages — Teacher
import TeacherDashboard from './pages/teacher/TeacherDashboard';
import ManageResults from './pages/teacher/ManageResults';
import UploadContent from './pages/teacher/UploadContent';
import ManageExams from './pages/teacher/ManageExams';

// Pages — Admin
import AdminDashboard from './pages/admin/AdminDashboard';
import ManageStudents from './pages/admin/ManageStudents';
import ManageTeachers from './pages/admin/ManageTeachers';
import ManageClasses from './pages/admin/ManageClasses';

// Pages — Public
import HomePage from './pages/public/HomePage';
import TeacherDirectory from './pages/public/TeacherDirectory';
import SchoolGallery from './pages/public/SchoolGallery';
import SchoolCalendar from './pages/public/SchoolCalendar';

// Layout
import AppLayout from './components/layout/AppLayout';
import ForbiddenPage from './pages/ForbiddenPage';

const theme = createTheme({
  palette: {
    primary: { main: '#1E3A5F' },
    secondary: { main: '#2980B9' },
    success: { main: '#27AE60' },
    warning: { main: '#E67E22' },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
  },
  shape: { borderRadius: 8 },
  components: {
    MuiButton: {
      styleOverrides: { root: { textTransform: 'none', fontWeight: 600 } },
    },
    MuiCard: {
      styleOverrides: { root: { boxShadow: '0 2px 8px rgba(0,0,0,0.08)' } },
    },
  },
});

const ADMIN_ROLES = ['SUPER_ADMIN', 'SCHOOL_ADMIN'];
const TEACHER_ROLES = ['CLASS_TEACHER', 'SUBJECT_TEACHER', 'SCHOOL_ADMIN', 'SUPER_ADMIN'];

function AppRoutes() {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(restoreSession());
  }, [dispatch]);

  return (
    <Routes>
      {/* Public */}
      <Route path="/" element={<HomePage />} />
      <Route path="/teachers" element={<TeacherDirectory />} />
      <Route path="/gallery" element={<SchoolGallery />} />
      <Route path="/calendar" element={<SchoolCalendar />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/403" element={<ForbiddenPage />} />

      {/* Protected — Student */}
      <Route path="/student" element={
        <ProtectedRoute allowedRoles={['STUDENT']}>
          <AppLayout role="student" />
        </ProtectedRoute>
      }>
        <Route path="dashboard" element={<StudentDashboard />} />
        <Route path="results" element={<StudentResults />} />
        <Route path="content" element={<StudentContent />} />
        <Route path="tests" element={<OnlineTest />} />
        <Route index element={<Navigate to="dashboard" replace />} />
      </Route>

      {/* Protected — Teacher */}
      <Route path="/teacher" element={
        <ProtectedRoute allowedRoles={TEACHER_ROLES}>
          <AppLayout role="teacher" />
        </ProtectedRoute>
      }>
        <Route path="dashboard" element={<TeacherDashboard />} />
        <Route path="results" element={<ManageResults />} />
        <Route path="content" element={<UploadContent />} />
        <Route path="exams" element={<ManageExams />} />
        <Route index element={<Navigate to="dashboard" replace />} />
      </Route>

      {/* Protected — Admin */}
      <Route path="/admin" element={
        <ProtectedRoute allowedRoles={ADMIN_ROLES}>
          <AppLayout role="admin" />
        </ProtectedRoute>
      }>
        <Route path="dashboard" element={<AdminDashboard />} />
        <Route path="students" element={<ManageStudents />} />
        <Route path="teachers" element={<ManageTeachers />} />
        <Route path="classes" element={<ManageClasses />} />
        <Route index element={<Navigate to="dashboard" replace />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

function App() {
  return (
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
        <ToastContainer position="top-right" autoClose={3000} />
      </ThemeProvider>
    </Provider>
  );
}

export default App;
