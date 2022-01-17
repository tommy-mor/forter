import useSWR from 'swr'

import { getUser } from '../api/login'

function useLogin() {
  const { data, mutate, error } = useSWR("/get_user", getUser, { suspense: true })

  const loading = !data && !error
  const loggedOut = error && error.status === 403

  return {
    loading,
    loggedOut,
    user: data,
    mutate
  }
}

export { useLogin }
