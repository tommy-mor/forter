import { useForm } from "react-hook-form"
import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Stack from '@mui/material/Stack'

export default function Register() {
  const { register, handleSubmit, formState: { errors } } = useForm()
  const onSubmit = data => console.log(data);

  return <Box
    component="form"
    sx={{
      '& .MuiTextField-root': { m: 1, width: '25ch' },
    }}
    noValidate
    autoComplete="off"
    onSubmit={handleSubmit(onSubmit)}
  >
    <Paper sx={{ padding: '1em' }}>
      <Stack alignItems="center" spacing={2}>
        <TextField
          required
          error={!!errors.username}
          id="outlined-required"
          label="Username"
          {...register("username", { required: true })}
        />
        <TextField
          required
          error={!!errors.password}
          id="outlined-required"
          label="Password"
          {...register("password", { required: true })}
        />
        <Button variant="contained" type="submit">
          Register
        </Button>
      </Stack>
    </Paper>
  </Box>
}
