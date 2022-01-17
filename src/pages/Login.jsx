import { useEffect } from 'react'
import { useNavigate } from "react-router-dom"
import { useForm } from 'react-hook-form'

import { login } from '../api/login'
import { useLogin } from '../hooks/login'

import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Stack from '@mui/material/Stack'

export default function Login() {
  const { register, handleSubmit, formState: { errors } } = useForm()
  const { user, loading, loggedOut, mutate } = useLogin()
  const navigate = useNavigate()

  useEffect(() => {
      if (user && !loggedOut) navigate("/")
  }, [ user, loggedOut, navigate ])

  function onSubmit({username, password}) {
      console.log("logging in")
      login(username, password)
      mutate()
  }

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
          Login
        </Button>
      </Stack>
    </Paper>
  </Box>
}
