import { NavLink, Outlet } from 'react-router-dom';

import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';

export default function Main() {
  return (
    <main>
      <Box sx={{ flexGrow: 1 }}>
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
              Sorter
            </Typography>
            <NavLink to="/users" >
              <Button color="inherit">Users</Button>
            </NavLink>
            <NavLink to="/Tags" >
              <Button color="inherit">Tags</Button>
            </NavLink>
            <NavLink to="/login" >
              <Button color="inherit">Login</Button>
            </NavLink>
            <NavLink to="/register" >
              <Button color="inherit">Register</Button>
            </NavLink>
          </Toolbar>
        </AppBar>
      </Box>
      <Outlet />
    </main>
  )
}
