import useSWR from 'swr'

import { getUser } from '../api/login'

function useLogin() {
  const { data, mutate, error } = useSWR("/get_user", getUser, { suspense: true })

  let cerror = error || data === "error"
  const loading = !data && !cerror
  const loggedOut = data === "error"

  return {
    loading,
    loggedOut,
    user: data,
    mutate
  }
}

export { useLogin }
