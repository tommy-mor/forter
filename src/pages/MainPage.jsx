import { Link as LocalLink, NavLink, Outlet } from 'react-router-dom'

import { useLogin } from '../hooks/login'
import { logout } from '../api/login'

import Link from '@mui/material/Link'
import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'

export default function Main() {
    const { user, loggedOut, mutate } = useLogin()

    function onLogout() {
        logout()
        mutate()
    }

    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar color="secondary" position="static">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        <Link component={LocalLink} to="/" >
                        sorter
                        </Link>
                    </Typography>
                    <Link component={NavLink} to="/users" >
                        <Button color="inherit">Users</Button>
                    </Link>
                    <Link component={NavLink} to="/tags" >
                        <Button color="inherit">Tags</Button>
                    </Link>
                    {!user || loggedOut ?
                    (<>
                        <Link component={NavLink} to="/login" >
                            <Button color="inherit">Login</Button>
                        </Link>
                        <Link component={NavLink} to="/register" >
                            <Button color="inherit">Register</Button>
                        </Link>
                        </>)
                    :(<>
                        <Link component={NavLink} to={`/tags/${user.name}`} >
                            <Button color="inherit">My tags</Button>
                        </Link>
                        <Button color="inherit" onClick={onLogout}>Logout</Button>
                        </>)}
                </Toolbar>
            </AppBar>
            <Outlet />
        </Box>
    )
}
