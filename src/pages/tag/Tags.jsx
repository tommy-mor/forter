import { Link } from 'react-router-dom'
import { useTags } from '../../hooks/tags'

import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'

import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Paper from '@mui/material/Paper'

export default function Tags() {
  const { tags, isLoading } = useTags()


  if (isLoading) return <div>loading</div>

  return (
    <Box>
        <TableContainer component={Paper}>
            <Table>
            <TableHead>
                <TableRow>
                    <TableCell>Votes</TableCell>
                    <TableCell>Items</TableCell>
                    <TableCell>Name</TableCell>
                    <TableCell>Creator</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {tags.map(({json: {id, title, votes, items, user}}) => (
                    <TableRow
                        key={id}
                        >
                        <TableCell>{votes}</TableCell>
                        <TableCell>{items}</TableCell>
                        <TableCell>
                            <Link to={`/tags/view/${id}`} >
                              <Typography>{title}</Typography>
                            </Link>
                        </TableCell>
                        <TableCell>
                            <Link to={`/users/${user.username}`} >
                              <Typography>{user.username}</Typography>
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
