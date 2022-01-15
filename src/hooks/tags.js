import useSWR from 'swr'
import { getTagById, getTags } from '../api/tags'

function useTag(tagId) {
  const { data, error } = useSWR(`tag/${tagId}`, () => getTagById(tagId))

  return {
    tag: data,
    isLoading: !error && !data,
    isError: error
  }
}


function useTags() {
  const { data, error } = useSWR(`tags`, getTags)

  return {
    tags: data,
    isLoading: !error && !data,
    isError: error
  }
}

export { useTag, useTags }
