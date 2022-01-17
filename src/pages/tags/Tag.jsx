import { useCallback, useState } from 'react'

import { useTag, useNextVote } from '../../hooks/tags'
import { useLogin } from '../../hooks/login'

import { useParams, Link as LocalLink } from 'react-router-dom'

import Paper from '@mui/material/Paper'
import FormControl from '@mui/material/FormControl'
import Select from '@mui/material/Select'
import InputLabel from '@mui/material/InputLabel'
import Button from '@mui/material/Button'
import MenuItem from '@mui/material/MenuItem'
import Link from '@mui/material/Link'
import Card from '@mui/material/Card'
import Box from '@mui/material/Box'
import Slider from '@mui/material/Slider'
import Stack from '@mui/material/Stack'
import Accordion from '@mui/material/Accordion'
import AccordionSummary from '@mui/material/AccordionSummary'
import AccordionDetails from '@mui/material/AccordionDetails'
import Typography from '@mui/material/Typography'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import { styled } from '@mui/material/styles'

function TagTitle( {name, description, numItems, numVotes, numUsers, creator } ) {
  return <Card sx={{ padding: '1rem', margin: '1rem' }}>
           <Typography>{name}</Typography>
           <Typography>{numItems} items with {numVotes} vote{numVotes === 1 ? '' : 's'} from {numUsers} users</Typography>
           <Typography>{description}</Typography>
           <Typography component="div">
             Created by{' '}
             <Link component={LocalLink} to={`/users/${creator}`} >
               {creator}
             </Link>
           </Typography>
         </Card>
}

function Item({ type, name }) {
   return <Typography sx={{
        padding: '1rem',
        textAlign: 'center',
        textColor: "black",
    }}
    >{name}</Typography>
}


const DEFAULT_RATING = 50

function PairwiseVote({ tagId }) {
  const { items, isLoading, submitVote, skipVote } = useNextVote(tagId)
  const [rating, setRating] = useState(DEFAULT_RATING) // state of the bar

  function onSlide(name) {
    return (e, value) => setRating(e.target.value)
  }

  const submit = useCallback(() => {
    submitVote(rating)
    setRating(DEFAULT_RATING)
  }, [ rating, submitVote ])

  const skip = useCallback(() => {
      skipVote()
      setRating(DEFAULT_RATING)
  }, [ skipVote ])

  if (isLoading) return <div>loading..</div>

  return <Card sx={{ padding: '1rem', margin: '1rem' }}>
          <Stack direction="row" justifyContent="space-between" spacing={2}>
            {items.map((item, i) => <Item key={i} {...item}/>)}
          </Stack>
          <Slider defaultValue={rating} step={10} marks min={0} max={100} onChange={onSlide} />
          <Stack direction="row" justifyContent="space-between" spacing={2}>
            <Button onClick={submit}>Vote</Button>
            <Button onClick={skip}>Skip</Button>
          </Stack>
        </Card>
}

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

// main tag page
export default function Tag() {
  const { tagId } = useParams()

  const { user, loggedOut } = useLogin()
  const { tag, isLoading } = useTag(tagId)

  if (isLoading) return <div>loading</div>

  const { name, description, contributors, votes, items, creator } = tag

  return (
    <Box>
      <TagTitle
        name={name}
        description={description}
        numItems={items.length}
        numVotes={votes.length}
        numUsers={contributors.length}
        creator={creator}
      />

      {/* only show if can vote */}
      {!loggedOut && <PairwiseVote />}
      <TagAccordion
          title="Ranked Items"
          items={items}
          users={contributors}
        />
        {items && <TagAccordion
          title="Unranked Items"
          items={items}
          users={contributors}
        />}
    </Box>
  )
}
