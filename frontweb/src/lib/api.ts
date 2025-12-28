import { supabase } from '@/integrations/supabase/client';

// const DOMAIN = 'https://blogtag-api.hobeenkim.com'
const DOMAIN = 'http://localhost:8080'

interface FetchOptions extends RequestInit {
  headers?: Record<string, string>;
}

const fetchWithAuth = async (url: string, options: FetchOptions = {}) => {
  const { data: { session } } = await supabase.auth.getSession();
  const token = session?.access_token;

  const headers = {
    ...options.headers,
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };

  const response = await fetch(`${DOMAIN}${url}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    throw new Error(`API Error: ${response.statusText}`);
  }

  return response.json();
};

export const getBookmarks = async (cursorTime?: string) => {
  const url = cursorTime 
    ? `/bookmarks/me?cursorTime=${cursorTime}` 
    : '/bookmarks/me';
  return fetchWithAuth(url);
};

export const addBookmark = async (postId: string) => {
  return fetchWithAuth(`/bookmarks/${postId}`, {
    method: 'POST',
  });
};

export const removeBookmark = async (postId: string) => {
  return fetchWithAuth(`/bookmarks/${postId}`, {
    method: 'DELETE',
  });
};

export const getLikes = async (cursorTime?: string) => {
  const url = cursorTime
      ? `/likes/me?cursorTime=${cursorTime}`
      : '/likes/me';
  return fetchWithAuth(url);
};

export const like = async (postId: string) => {
  return fetchWithAuth(`/likes/${postId}`, {
    method: 'POST',
  });
};

export const removeLike = async (postId: string) => {
  return fetchWithAuth(`/likes/${postId}`, {
    method: 'DELETE',
  });
};

export const getPosts = async (page: number) => {
  return fetchWithAuth(`/posts?page=${page}`);
};

export const getComments = async (postId: string, cursorTime?: string) => {
  const url = cursorTime
    ? `/comments/posts/${postId}?cursorTime=${cursorTime}`
    : `/comments/posts/${postId}`;
  return fetchWithAuth(url);
};

export const createComment = async (postId: string, comment: string) => {
  return fetchWithAuth(`/comments/posts/${postId}`, {
    method: 'POST',
    body: JSON.stringify({ comment }),
  });
};

export const deleteComment = async (commentId: number) => {
  return fetchWithAuth(`/comments/${commentId}`, {
    method: 'DELETE',
  });
};

export const updateComment = async (commentId: number, comment: string) => {
  return fetchWithAuth(`/comments/${commentId}`, {
    method: 'PUT',
    body: JSON.stringify({ comment }),
  });
};
