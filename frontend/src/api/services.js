import apiClient from './apiClient';

// ── AUTH ─────────────────────────────────────────────────────────
export const authApi = {
  login: (credentials) => apiClient.post('/auth/login', credentials),
  logout: (refreshToken) => apiClient.post('/auth/logout', { refreshToken }),
  refresh: (refreshToken) => apiClient.post('/auth/refresh', { refreshToken }),
  register: (userData) => apiClient.post('/auth/register', userData),
  changePassword: (data) => apiClient.put('/auth/password', data),
};

// ── STUDENTS ──────────────────────────────────────────────────────
export const studentApi = {
  getAll: (page = 0, size = 20) => apiClient.get(`/students?page=${page}&size=${size}`),
  getById: (id) => apiClient.get(`/students/${id}`),
  getByClass: (classId) => apiClient.get(`/students/class/${classId}`),
  getByAdmissionNumber: (admNo) => apiClient.get(`/students/admission/${admNo}`),
  create: (data) => apiClient.post('/students', data),
  update: (id, data) => apiClient.put(`/students/${id}`, data),
  promote: (studentId, newClassId) => apiClient.put(`/students/${studentId}/promote/${newClassId}`),
};

// ── ACADEMIC (Exams & Results) ────────────────────────────────────
export const academicApi = {
  createExam: (data) => apiClient.post('/academic/exams', data),
  getExamsByClass: (classId, year) => apiClient.get(`/academic/exams/class/${classId}?year=${year}`),
  saveResult: (examId, data) => apiClient.post(`/academic/exams/${examId}/results`, data),
  bulkSaveResults: (examId, data) => apiClient.post(`/academic/exams/${examId}/results/bulk`, data),
  publishResults: (examId) => apiClient.put(`/academic/exams/${examId}/publish`),
  getStudentResults: (studentId, year) => apiClient.get(`/academic/results/student/${studentId}?year=${year}`),
  getClassResults: (examId) => apiClient.get(`/academic/exams/${examId}/results`),
  getReportCard: (studentId, year) => apiClient.get(`/academic/report-card/${studentId}?year=${year}`),
};

// ── CONTENT ───────────────────────────────────────────────────────
export const contentApi = {
  getMyContent: () => apiClient.get('/content/my-content'),
  getClassContent: (classId) => apiClient.get(`/content/class/${classId}`),
  getStudentContent: () => apiClient.get('/content/student'), // Uses JWT claims
  upload: (data) => apiClient.post('/content/upload', data),
  delete: (contentId) => apiClient.delete(`/content/${contentId}`),
};

// ── TEACHERS ──────────────────────────────────────────────────────
export const teacherApi = {
  getAll: () => apiClient.get('/teachers'),
  getPublicProfiles: () => apiClient.get('/teachers/public'),
  getById: (id) => apiClient.get(`/teachers/${id}`),
  create: (data) => apiClient.post('/teachers', data),
  update: (id, data) => apiClient.put(`/teachers/${id}`, data),
  addQualification: (id, data) => apiClient.post(`/teachers/${id}/qualifications`, data),
  addAchievement: (id, data) => apiClient.post(`/teachers/${id}/achievements`, data),
};

// ── CALENDAR ──────────────────────────────────────────────────────
export const calendarApi = {
  getEvents: (month, year) => apiClient.get(`/calendar/events?month=${month}&year=${year}`),
  createEvent: (data) => apiClient.post('/calendar/events', data),
  updateEvent: (id, data) => apiClient.put(`/calendar/events/${id}`, data),
  deleteEvent: (id) => apiClient.delete(`/calendar/events/${id}`),
};

// ── ONLINE TESTS ──────────────────────────────────────────────────
export const testApi = {
  getAvailableTests: () => apiClient.get('/tests/available'),
  getTestById: (id) => apiClient.get(`/tests/${id}`),
  createTest: (data) => apiClient.post('/tests', data),
  startAttempt: (testId) => apiClient.post(`/tests/${testId}/attempt`),
  submitAttempt: (testId, attemptId, answers) =>
    apiClient.post(`/tests/${testId}/attempt/${attemptId}/submit`, { answers }),
  getMyAttempts: () => apiClient.get('/tests/my-attempts'),
  getTestResults: (testId) => apiClient.get(`/tests/${testId}/results`),
};
