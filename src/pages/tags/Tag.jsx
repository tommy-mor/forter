import { useState } from 'react'
import { useTag } from '../../hooks/tags'


import { useParams, Link as LocalLink, NavLink, Outlet } from 'react-router-dom'

import Link from '@mui/material/Link'
import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import Toolbar from '@mui/material/Toolbar'
import Button from '@mui/material/Button'
import Slider from '@mui/material/Slider'
import Stack from '@mui/material/Stack'
import Accordion from '@mui/material/Accordion'
import AccordionSummary from '@mui/material/AccordionSummary'
import AccordionDetails from '@mui/material/AccordionDetails'
import Typography from '@mui/material/Typography'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'

function TagTitle( {name, description, numItems, numVotes, numUsers, creator } ) {
  return <Box>
           <Typography>Showing tag "{name}": {numItems} items with {numVotes} votes from {numUsers} users</Typography>
           <Typography component="div">
             Created by{' '}
             <Link component={LocalLink} to={`/users/${creator}`} >
               {creator}
             </Link>
           </Typography>
         </Box>
}

function PairwiseVote() {

  const [level, setLevel] = useState(50) // state of the bar

  function onSlide(name) {
    return (e, value) => setLevel(e.target.value)
  }

  return<Box><Stack direction="row" spacing={2}>
              <div>rast</div>
              <div>dndn</div>
            </Stack>
         <Slider defaultValue={level} onChange={onSlide} />
       </Box>

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
      <PairwiseVote/>
      <Accordion>
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

      <Accordion>
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
