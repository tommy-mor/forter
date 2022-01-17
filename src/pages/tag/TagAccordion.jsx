import { useState } from 'react'

import FormControl from '@mui/material/FormControl'
import Select from '@mui/material/Select'
import InputLabel from '@mui/material/InputLabel'
import MenuItem from '@mui/material/MenuItem'
import Accordion from '@mui/material/Accordion'
import AccordionSummary from '@mui/material/AccordionSummary'
import AccordionDetails from '@mui/material/AccordionDetails'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'

import Typography from '@mui/material/Typography'
import Paper from '@mui/material/Paper'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'


function TagItemTable({ items }) {
    return (
        <TableContainer component={Paper}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Score</TableCell>
                        <TableCell>Votes</TableCell>
                        <TableCell>Name</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                {items.map(({ score, votes, name }) =>
                    <TableRow key={name} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                        <TableCell> {score} </TableCell>
                        <TableCell> {votes} </TableCell>
                        <TableCell> {name} </TableCell>
                    </TableRow>)}
                </TableBody>
            </Table>
        </TableContainer>
    )
}


function TagAccordion( { title, items, users }) {
    const [curUser, setCurUser] = useState('')

    function onUserSelect(e) {
        setCurUser(e.target.value)
    }

    const curItems = curUser === '' ? items : items.filter(({ creator }) => creator === curUser)

    return (
        <Accordion sx={{ margin: '1rem' }}>
            <AccordionSummary
              expandIcon={<ExpandMoreIcon/>}
            >
              <Typography>{title}</Typography>
            </AccordionSummary>
            <AccordionDetails>
              <FormControl fullWidth>
                <InputLabel>User</InputLabel>
                <Select
                  defaultValue={''}
                  value={curUser}
                  label="By user"
                  onChange={onUserSelect}
                >
                  <MenuItem value={''} key={-1}>All Users</MenuItem>
                  {users.map((user, i) =>
                    (<MenuItem value={user} key={i}>{user}</MenuItem>))}
                </Select>
              </FormControl>
                <TagItemTable items={curItems} />
            </AccordionDetails>
        </Accordion>
    )
}

export default TagAccordion
