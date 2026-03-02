import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { jwtDecode } from 'jwt-decode';
import { authApi } from '../../api/services';

export const login = createAsyncThunk('auth/login', async (credentials, { rejectWithValue }) => {
  try {
    const { data } = await authApi.login(credentials);
    return data;
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Login failed');
  }
});

export const logout = createAsyncThunk('auth/logout', async (_, { getState }) => {
  const { refreshToken } = getState().auth;
  if (refreshToken) await authApi.logout(refreshToken).catch(() => {});
});

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user: null,
    accessToken: localStorage.getItem('accessToken'),
    refreshToken: localStorage.getItem('refreshToken'),
    isAuthenticated: !!localStorage.getItem('accessToken'),
    loading: false,
    error: null,
  },
  reducers: {
    clearError: (state) => { state.error = null; },
    restoreSession: (state) => {
      const token = localStorage.getItem('accessToken');
      if (token) {
        try {
          const decoded = jwtDecode(token);
          if (decoded.exp * 1000 > Date.now()) {
            state.accessToken = token;
            state.refreshToken = localStorage.getItem('refreshToken');
            state.user = {
              id: decoded.sub,
              role: decoded.role,
              classId: decoded.classId,
              subjectIds: decoded.subjectIds,
              email: decoded.email,
            };
            state.isAuthenticated = true;
          } else {
            localStorage.clear();
          }
        } catch {
          localStorage.clear();
        }
      }
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(login.fulfilled, (state, { payload }) => {
        state.loading = false;
        state.accessToken = payload.accessToken;
        state.refreshToken = payload.refreshToken;
        state.isAuthenticated = true;
        state.user = {
          id: payload.userId,
          username: payload.username,
          email: payload.email,
          role: payload.role,
          classId: payload.classId,
        };
        localStorage.setItem('accessToken', payload.accessToken);
        localStorage.setItem('refreshToken', payload.refreshToken);
      })
      .addCase(login.rejected, (state, { payload }) => {
        state.loading = false;
        state.error = payload;
      })
      .addCase(logout.fulfilled, (state) => {
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.isAuthenticated = false;
        localStorage.clear();
      });
  },
});

export const { clearError, restoreSession } = authSlice.actions;
export default authSlice.reducer;

// Selectors
export const selectUser = (state) => state.auth.user;
export const selectRole = (state) => state.auth.user?.role;
export const selectIsAuthenticated = (state) => state.auth.isAuthenticated;
export const selectAuthLoading = (state) => state.auth.loading;
export const selectAuthError = (state) => state.auth.error;
