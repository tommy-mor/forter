import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Stack from '@mui/material/Stack'

export default function Register() {
  return <Box
    component="form"
    sx={{
      '& .MuiTextField-root': { m: 1, width: '25ch' },
    }}
    noValidate
    autoComplete="off"
  >
    <Paper sx={{ padding: '1em' }}>
      <Stack alignItems="center" spacing={2}>
        <TextField
          required
          id="outlined-required"
          label="Username"
        />
        <TextField
          required
          id="outlined-required"
          label="Password"
        />
        <Button variant="contained">
          Register
        </Button>
      </Stack>
    </Paper>
  </Box>
}
