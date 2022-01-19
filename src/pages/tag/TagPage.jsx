import { Suspense } from 'react'
import { Outlet } from 'react-router-dom'

import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'
import Link from '@mui/material/Link'
import Typography from '@mui/material/Typography'
import Stack from '@mui/material/Stack'

export default function TagPage() {
  return (
      <Suspense fallback={<div>Loading tags...</div>}>
        <Box>
          <Paper sx={{ padding: '1em' }}>
            <Stack direction="row" justifyContent="space-between">
                <Typography>Tags</Typography>
                <Link to="/tags/new" >
                    <Typography>New</Typography>
                </Link>
            </Stack>
            <Outlet />
          </Paper>
        </Box>
</Suspense>
  )
}
