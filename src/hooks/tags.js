import { useEffect } from 'react'
import { useLogin } from './login'

import useSWR from 'swr'
import { getTagById, getTags, getNextVote } from '../api/tags'

function useTag(tagId) {
  const { data, error } = useSWR(`tag/${tagId}`, () => getTagById(tagId), { suspense: true })

  return {
    tag: data,
    isLoading: !error && !data,
    isError: error
  }
}

function useTags() {
  const { user } = useLogin()
  const { data, error, mutate } = useSWR(['tags', user], (_, tags) => getTags(user), { suspense: true })

  useEffect(mutate, [ user, mutate ])

  return {
    tags: data,
    isLoading: !error && !data,
    isError: error
  }
}

function useNextVote(tagId) {
  const { data, error, mutate } = useSWR(`nextVote`, () => getNextVote(tagId), { suspense: true })

  function submitVote(val) {
    console.log(`submitting vote with val ${val}`)
    mutate()
  }

  function skipVote() {
    console.log(`skipping vote`)
    mutate()
  }

  return {
    items: data,
    isLoading: !error && !data,
    isError: error,
    submitVote,
    skipVote
  }
}

export { useTag, useTags, useNextVote }
