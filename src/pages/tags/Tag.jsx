import { useState } from 'react'
import { useTag } from '../../hooks/tags'


import { useParams, Link as LocalLink, NavLink, Outlet } from 'react-router-dom'

import Paper from '@mui/material/Paper'
import Button from '@mui/material/Button'
import Link from '@mui/material/Link'
import Card from '@mui/material/Card'
import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import Toolbar from '@mui/material/Toolbar'
import Slider from '@mui/material/Slider'
import Stack from '@mui/material/Stack'
import Accordion from '@mui/material/Accordion'
import AccordionSummary from '@mui/material/AccordionSummary'
import AccordionDetails from '@mui/material/AccordionDetails'
import Typography from '@mui/material/Typography'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'
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

const Item = styled(Paper)(({ theme }) => ({
  ...theme.typography.body2,
  padding: theme.spacing(1),
  textAlign: 'center',
  color: theme.palette.text.secondary,
}));


function PairwiseVote() {
  // useVote hook: gives you the next vote depending on who you are,
  // also lets you submit or pass on a vote with two buttons
  const [level, setLevel] = useState(50) // state of the bar

  function onSlide(name) {
    return (e, value) => setLevel(e.target.value)
  }

  return<Card sx={{ padding: '1rem', margin: '1rem' }}>
          <Stack direction="row" justifyContent="space-between" spacing={2}>
            <Item>item1</Item>
            <Item>item2</Item>
          </Stack>
          <Slider defaultValue={level} step={10} marks min={0} max={100} onChange={onSlide} />
          <Stack direction="row" justifyContent="space-between" spacing={2}>
            <Button>Vote</Button>
            <Button>Pass</Button>
          </Stack>
        </Card>
}

// main tag page
export default function Tag() {
  const { tagId } = useParams()
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
      <PairwiseVote />

      <Accordion sx={{ margin: '1rem' }}>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon/>}
        >
          <Typography>Ranking</Typography>
        </AccordionSummary>
        <AccordionDetails>
          {items.map(({ score, votes, name }) =>
            <div key={name}>{score} {votes} {name}</div>
          )}
        </AccordionDetails>
      </Accordion>

      <Accordion sx={{ margin: '1rem' }}>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon/>}
        >
          <Typography>Unranked Items</Typography>
        </AccordionSummary>
        <AccordionDetails>
          {items.map(({ score, votes, name }) =>
            <div key={name}>{score} {votes} {name}</div>
          )}
        </AccordionDetails>
      </Accordion>
    </Box>
  )
}
