export interface SliceCommentResponse {
  sliceInfo: Comment;
  data: Comment[];
}

export interface SliceInfo {
  size: number;
  hasNext: boolean;
}

export interface Comment {
  commentId: number;
  name: string;
  userId: string;
  comment: string;
  isMyComment: boolean;
  createdAt: string;
}