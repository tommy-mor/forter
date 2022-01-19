import useSWR from 'swr'
import { getUserById, getUsers } from '../api/users'

function useUser(userId) {
  const { data, error } = useSWR(`user/${userId}`, () => getUserById(userId), { suspense: true })

  return {
    user: data,
    isLoading: !error && !data,
    isError: error
  }
}


function useUsers() {
  const { data, error } = useSWR(`users`, getUsers, { suspense: true })

  return {
    users: data,
    isLoading: !error && !data,
    isError: error
  }
}

export { useUser, useUsers }
