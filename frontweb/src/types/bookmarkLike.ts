export interface SliceBookmarkResponse {
  sliceInfo: SliceInfo;
  data: PostBookmark[];
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
  bookmarked: boolean;
  bookmarkedTime: string;
}
