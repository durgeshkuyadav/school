import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {
  Box, Card, CardContent, TextField, Button, Typography,
  CircularProgress, Alert, Avatar, Container
} from '@mui/material';
import SchoolIcon from '@mui/icons-material/School';
import { login, clearError, selectAuthLoading, selectAuthError, selectIsAuthenticated, selectRole } from '../../store/slices/authSlice';

const schema = yup.object({
  username: yup.string().required('Username is required'),
  password: yup.string().required('Password is required'),
});

const ROLE_HOME = {
  SUPER_ADMIN: '/admin/dashboard',
  SCHOOL_ADMIN: '/admin/dashboard',
  CLASS_TEACHER: '/teacher/dashboard',
  SUBJECT_TEACHER: '/teacher/dashboard',
  STUDENT: '/student/dashboard',
  PARENT: '/parent/dashboard',
};

const LoginPage = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const loading = useSelector(selectAuthLoading);
  const error = useSelector(selectAuthError);
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const role = useSelector(selectRole);

  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    if (isAuthenticated && role) {
      const from = location.state?.from?.pathname || ROLE_HOME[role] || '/';
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, role, navigate, location]);

  const onSubmit = (data) => {
    dispatch(clearError());
    dispatch(login(data));
  };

  return (
    <Box sx={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #1E3A5F 0%, #2980B9 100%)',
      display: 'flex', alignItems: 'center', justifyContent: 'center'
    }}>
      <Container maxWidth="xs">
        <Card elevation={12} sx={{ borderRadius: 3 }}>
          <CardContent sx={{ p: 4 }}>
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 3 }}>
              <Avatar sx={{ bgcolor: '#1E3A5F', width: 64, height: 64, mb: 2 }}>
                <SchoolIcon sx={{ fontSize: 36 }} />
              </Avatar>
              <Typography variant="h5" fontWeight="bold" color="#1E3A5F">
                School Portal
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Sign in to your account
              </Typography>
            </Box>

            {error && (
              <Alert severity="error" sx={{ mb: 2 }} onClose={() => dispatch(clearError())}>
                {error}
              </Alert>
            )}

            <form onSubmit={handleSubmit(onSubmit)}>
              <TextField
                {...register('username')}
                label="Username"
                fullWidth
                margin="normal"
                error={!!errors.username}
                helperText={errors.username?.message}
                autoFocus
              />
              <TextField
                {...register('password')}
                label="Password"
                type="password"
                fullWidth
                margin="normal"
                error={!!errors.password}
                helperText={errors.password?.message}
              />
              <Button
                type="submit"
                variant="contained"
                fullWidth
                size="large"
                disabled={loading}
                sx={{ mt: 2, py: 1.5, bgcolor: '#1E3A5F', '&:hover': { bgcolor: '#2980B9' } }}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : 'Sign In'}
              </Button>
            </form>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
};

export default LoginPage;
