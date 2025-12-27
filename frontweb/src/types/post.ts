export interface PagedResponse {
  pageInfo: PageInfo;
  data: Post[];
}

export interface Response {
  data: Post[];
}

export interface PageInfo {
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
  isLast: boolean;
  isFirst: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface Post {
  postId: string;
  title: string;
  description: string;
  url: string;
  source: string;
  pubDate: string;
  readTime: string;
  tags: string[];
  thumbnail?: string;
  bookmarked: boolean;
  bookmarkCount: number;
  liked: boolean;
  likeCount: number;
  comments: number;
}
