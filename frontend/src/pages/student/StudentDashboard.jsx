import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import {
  Grid, Card, CardContent, Typography, Box, Chip,
  CircularProgress, List, ListItem, ListItemText, Divider, Avatar
} from '@mui/material';
import SchoolIcon from '@mui/icons-material/School';
import AssignmentIcon from '@mui/icons-material/Assignment';
import QuizIcon from '@mui/icons-material/Quiz';
import ArticleIcon from '@mui/icons-material/Article';
import { academicApi, contentApi, testApi } from '../../api/services';
import { selectUser } from '../../store/slices/authSlice';

const StatCard = ({ icon, label, value, color }) => (
  <Card elevation={2} sx={{ borderRadius: 2, borderLeft: `4px solid ${color}` }}>
    <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
      <Avatar sx={{ bgcolor: color + '20', color }}>{icon}</Avatar>
      <Box>
        <Typography variant="h4" fontWeight="bold" color={color}>{value}</Typography>
        <Typography variant="body2" color="text.secondary">{label}</Typography>
      </Box>
    </CardContent>
  </Card>
);

const StudentDashboard = () => {
  const user = useSelector(selectUser);
  const currentYear = new Date().getFullYear() + '-' + (new Date().getFullYear() + 1);

  const [results, setResults] = useState([]);
  const [content, setContent] = useState([]);
  const [tests, setTests] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [resultsRes, contentRes, testsRes] = await Promise.allSettled([
          academicApi.getStudentResults(user.id, currentYear),
          contentApi.getStudentContent(),
          testApi.getAvailableTests(),
        ]);
        if (resultsRes.status === 'fulfilled') setResults(resultsRes.value.data);
        if (contentRes.status === 'fulfilled') setContent(contentRes.value.data);
        if (testsRes.status === 'fulfilled') setTests(testsRes.value.data);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [user.id, currentYear]);

  const passedExams = results.filter(r => r.examCleared).length;
  const avgGrade = results.length > 0
    ? (results.reduce((sum, r) => sum + (r.marksObtained / r.totalMarks) * 100, 0) / results.length).toFixed(1)
    : 0;

  if (loading) return (
    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
      <CircularProgress size={48} />
    </Box>
  );

  return (
    <Box>
      <Typography variant="h5" fontWeight="bold" mb={3} color="#1E3A5F">
        Welcome back, {user.username} 👋
      </Typography>

      {/* Stats Row */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard icon={<SchoolIcon />} label="Exams Taken" value={results.length} color="#1E3A5F" />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard icon={<AssignmentIcon />} label="Exams Cleared" value={passedExams} color="#27AE60" />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard icon={<ArticleIcon />} label="Study Materials" value={content.length} color="#2980B9" />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard icon={<QuizIcon />} label="Tests Available" value={tests.length} color="#E67E22" />
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        {/* Recent Results */}
        <Grid item xs={12} md={6}>
          <Card elevation={2} sx={{ borderRadius: 2 }}>
            <CardContent>
              <Typography variant="h6" fontWeight="bold" mb={2}>Recent Results</Typography>
              {results.length === 0 ? (
                <Typography color="text.secondary">No results published yet.</Typography>
              ) : (
                <List dense>
                  {results.slice(0, 5).map((r, i) => (
                    <React.Fragment key={r.id}>
                      <ListItem sx={{ px: 0 }}>
                        <ListItemText
                          primary={r.examName}
                          secondary={r.subjectName}
                        />
                        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                          <Typography fontWeight="bold">
                            {r.marksObtained}/{r.totalMarks}
                          </Typography>
                          <Chip
                            label={r.grade}
                            size="small"
                            color={r.examCleared ? 'success' : 'error'}
                          />
                        </Box>
                      </ListItem>
                      {i < 4 && <Divider />}
                    </React.Fragment>
                  ))}
                </List>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Recent Study Materials */}
        <Grid item xs={12} md={6}>
          <Card elevation={2} sx={{ borderRadius: 2 }}>
            <CardContent>
              <Typography variant="h6" fontWeight="bold" mb={2}>Recent Study Materials</Typography>
              {content.length === 0 ? (
                <Typography color="text.secondary">No materials uploaded yet.</Typography>
              ) : (
                <List dense>
                  {content.slice(0, 5).map((c, i) => (
                    <React.Fragment key={c.id}>
                      <ListItem sx={{ px: 0 }}>
                        <ListItemText
                          primary={c.title}
                          secondary={`${c.uploaderName} · ${c.scope === 'CLASS_WIDE' ? 'Class' : c.subjectName}`}
                        />
                        <Chip label={c.contentType.replace('_', ' ')} size="small" variant="outlined" />
                      </ListItem>
                      {i < 4 && <Divider />}
                    </React.Fragment>
                  ))}
                </List>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Available Tests */}
        {tests.length > 0 && (
          <Grid item xs={12}>
            <Card elevation={2} sx={{ borderRadius: 2, border: '2px solid #E67E22' }}>
              <CardContent>
                <Typography variant="h6" fontWeight="bold" mb={2} color="#E67E22">
                  🧪 Tests Available — Attempt Now
                </Typography>
                <Grid container spacing={2}>
                  {tests.map((t) => (
                    <Grid item xs={12} sm={6} md={4} key={t.id}>
                      <Card variant="outlined" sx={{ borderRadius: 2 }}>
                        <CardContent>
                          <Typography fontWeight="bold">{t.title}</Typography>
                          <Typography variant="body2" color="text.secondary">{t.subjectName}</Typography>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1 }}>
                            <Chip label={`${t.durationMinutes} min`} size="small" />
                            <Chip label={`${t.totalMarks} marks`} size="small" color="primary" />
                          </Box>
                        </CardContent>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              </CardContent>
            </Card>
          </Grid>
        )}
      </Grid>
    </Box>
  );
};

export default StudentDashboard;
