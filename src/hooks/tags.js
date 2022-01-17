import useSWR from 'swr'
import { getTagById, getTags, getNextVote } from '../api/tags'
import { useLogin } from './login'

function useTag(tagId) {
  const { data, error } = useSWR(`tag/${tagId}`, () => getTagById(tagId))

  return {
    tag: data,
    isLoading: !error && !data,
    isError: error
  }
}

function useTags(user) {
  const { data, error } = useSWR(`tags`, () => getTags(user))

  return {
    tags: data,
    isLoading: !error && !data,
    isError: error
  }
}

function useNextVote(tagId) {
  const { data, error, mutate } = useSWR(`nextVote`, () => getNextVote(tagId))

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
