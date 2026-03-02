import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {
  Box, Drawer, AppBar, Toolbar, List, ListItem, ListItemButton,
  ListItemIcon, ListItemText, Typography, IconButton, Avatar,
  Menu, MenuItem, Divider, useTheme, useMediaQuery, Tooltip
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import DashboardIcon from '@mui/icons-material/Dashboard';
import AssignmentIcon from '@mui/icons-material/Assignment';
import ArticleIcon from '@mui/icons-material/Article';
import QuizIcon from '@mui/icons-material/Quiz';
import PeopleIcon from '@mui/icons-material/People';
import SchoolIcon from '@mui/icons-material/School';
import ClassIcon from '@mui/icons-material/Class';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import LogoutIcon from '@mui/icons-material/Logout';
import PersonIcon from '@mui/icons-material/Person';
import { logout, selectUser } from '../../store/slices/authSlice';

const DRAWER_WIDTH = 240;

const NAV_ITEMS = {
  student: [
    { label: 'Dashboard', icon: <DashboardIcon />, path: '/student/dashboard' },
    { label: 'My Results', icon: <AssignmentIcon />, path: '/student/results' },
    { label: 'Study Materials', icon: <ArticleIcon />, path: '/student/content' },
    { label: 'Online Tests', icon: <QuizIcon />, path: '/student/tests' },
  ],
  teacher: [
    { label: 'Dashboard', icon: <DashboardIcon />, path: '/teacher/dashboard' },
    { label: 'Manage Exams', icon: <AssignmentIcon />, path: '/teacher/exams' },
    { label: 'Update Results', icon: <SchoolIcon />, path: '/teacher/results' },
    { label: 'Upload Content', icon: <ArticleIcon />, path: '/teacher/content' },
  ],
  admin: [
    { label: 'Dashboard', icon: <DashboardIcon />, path: '/admin/dashboard' },
    { label: 'Students', icon: <PeopleIcon />, path: '/admin/students' },
    { label: 'Teachers', icon: <PersonIcon />, path: '/admin/teachers' },
    { label: 'Classes', icon: <ClassIcon />, path: '/admin/classes' },
    { label: 'Calendar', icon: <CalendarMonthIcon />, path: '/admin/calendar' },
  ],
};

const AppLayout = ({ role }) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();
  const user = useSelector(selectUser);

  const navItems = NAV_ITEMS[role] || [];

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  const drawer = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Logo */}
      <Box sx={{ p: 2, background: 'linear-gradient(135deg, #1E3A5F, #2980B9)', color: 'white' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <SchoolIcon sx={{ fontSize: 32 }} />
          <Box>
            <Typography variant="subtitle1" fontWeight="bold" lineHeight={1}>
              School Portal
            </Typography>
            <Typography variant="caption" sx={{ opacity: 0.8, textTransform: 'capitalize' }}>
              {role} Panel
            </Typography>
          </Box>
        </Box>
      </Box>

      {/* Nav Items */}
      <List sx={{ flex: 1, pt: 1 }}>
        {navItems.map((item) => {
          const isActive = location.pathname === item.path;
          return (
            <ListItem key={item.path} disablePadding>
              <ListItemButton
                onClick={() => { navigate(item.path); if (isMobile) setMobileOpen(false); }}
                sx={{
                  mx: 1, borderRadius: 2, mb: 0.5,
                  bgcolor: isActive ? 'primary.main' : 'transparent',
                  color: isActive ? 'white' : 'text.primary',
                  '&:hover': {
                    bgcolor: isActive ? 'primary.dark' : 'action.hover',
                  },
                }}
              >
                <ListItemIcon sx={{ color: isActive ? 'white' : 'text.secondary', minWidth: 40 }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText primary={item.label} />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>

      <Divider />
      <Box sx={{ p: 2 }}>
        <Typography variant="caption" color="text.secondary">
          Logged in as: <strong>{user?.username}</strong>
        </Typography>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: '#F5F7FA' }}>
      {/* AppBar */}
      <AppBar position="fixed" sx={{ zIndex: theme.zIndex.drawer + 1, bgcolor: '#1E3A5F' }}>
        <Toolbar>
          {isMobile && (
            <IconButton color="inherit" edge="start" onClick={() => setMobileOpen(!mobileOpen)} sx={{ mr: 2 }}>
              <MenuIcon />
            </IconButton>
          )}
          <Typography variant="h6" fontWeight="bold" sx={{ flexGrow: 1 }}>
            School Management Portal
          </Typography>
          <Tooltip title="Account">
            <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} color="inherit">
              <Avatar sx={{ width: 32, height: 32, bgcolor: '#2980B9', fontSize: 14 }}>
                {user?.username?.[0]?.toUpperCase()}
              </Avatar>
            </IconButton>
          </Tooltip>
          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={() => setAnchorEl(null)}>
            <MenuItem disabled>
              <Typography variant="body2" color="text.secondary">{user?.email}</Typography>
            </MenuItem>
            <Divider />
            <MenuItem onClick={handleLogout}>
              <LogoutIcon fontSize="small" sx={{ mr: 1 }} /> Logout
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      {/* Sidebar */}
      <Box component="nav" sx={{ width: { md: DRAWER_WIDTH }, flexShrink: { md: 0 } }}>
        <Drawer
          variant={isMobile ? 'temporary' : 'permanent'}
          open={isMobile ? mobileOpen : true}
          onClose={() => setMobileOpen(false)}
          ModalProps={{ keepMounted: true }}
          sx={{
            '& .MuiDrawer-paper': {
              width: DRAWER_WIDTH,
              boxSizing: 'border-box',
              boxShadow: '2px 0 8px rgba(0,0,0,0.08)',
            },
          }}
        >
          {drawer}
        </Drawer>
      </Box>

      {/* Main Content */}
      <Box component="main" sx={{
        flexGrow: 1,
        width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
        mt: 8, p: 3,
      }}>
        <Outlet />
      </Box>
    </Box>
  );
};

export default AppLayout;
