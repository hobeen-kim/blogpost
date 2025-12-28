export interface SliceBookmarkResponse {
  sliceInfo: SliceInfo;
  data: PostBookmark[];
}

export interface SliceLikeResponse {
  sliceInfo: SliceInfo;
  data: PostLike[];
}

export interface SliceInfo {
  size: number;
  hasNext: boolean;
}

export interface PostBookmark {
  postId: string;
  title: string;
  description: string;
  url: string;
  source: string;
  pubDate: string;
  tags: string[];
  thumbnail?: string;
  bookmarkedTime: string;
  metadata: object;
}

export interface PostLike {
  postId: string;
  title: string;
  description: string;
  url: string;
  source: string;
  pubDate: string;
  tags: string[];
  thumbnail?: string;
  likedTime: string;
  metadata: object;
}
