import useSWR from 'swr'
import { getUserById, getUsers } from '../api/users'

function useUser(userId) {
  const { data, error } = useSWR(`user/${userId}`, () => getUserById(userId))

  return {
    user: data,
    isLoading: !error && !data,
    isError: error
  }
}


function useUsers() {
  const { data, error } = useSWR(`users`, getUsers)

  return {
    users: data,
    isLoading: !error && !data,
    isError: error
  }
}

export { useUser, useUsers }
