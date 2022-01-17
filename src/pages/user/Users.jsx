import { Link } from 'react-router-dom'
import { useUsers } from '../../hooks/users'

import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'

import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableRow from '@mui/material/TableRow'
import Paper from '@mui/material/Paper'

export default function Users() {
  const { users, isLoading } = useUsers()

  if (isLoading) return <div>loading</div>

  return (
      <Box>
        <TableContainer component={Paper}>
            <Table>
            <TableBody>
                {users.map(({ name }) => (
                    <TableRow
                        key={name}
                        >
                        <TableCell>
                            <Link to={`/users/${name}`} >
                              <Typography>{name}</Typography>
                            </Link>
                        </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
            </Table>
        </TableContainer>
    </Box>
  )
}
