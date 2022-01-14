import { Link as LocalLink, NavLink, Outlet } from 'react-router-dom'

import Link from '@mui/material/Link'
import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'

export default function Main() {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar color="secondary" position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            <Link component={LocalLink} to="/" >
              Sorter
            </Link>
          </Typography>
          <Link component={NavLink} to="/users" >
            <Button color="inherit">Users</Button>
          </Link>
          <Link component={NavLink} to="/tags" >
            <Button color="inherit">Tags</Button>
          </Link>
          <Link component={NavLink} to="/login" >
            <Button color="inherit">Login</Button>
          </Link>
          <Link component={NavLink} to="/register" >
            <Button color="inherit">Register</Button>
          </Link>
        </Toolbar>
      </AppBar>
      <Outlet />
    </Box>
  )
}
