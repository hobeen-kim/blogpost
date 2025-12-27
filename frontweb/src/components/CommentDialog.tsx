import React, { useState, useEffect, useRef, useCallback } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useAuth } from "@/contexts/AuthContext";
import { useToast } from "@/hooks/use-toast";
import { Loader2, MoreVertical, Trash2, Edit2, X, Check } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { getComments, createComment, deleteComment, updateComment } from '@/lib/api';
import { Comment, SliceCommentResponse } from '@/types/comment';
import { cn } from "@/lib/utils";

interface CommentDialogProps {
  postId: string;
  isOpen: boolean;
  onClose: () => void;
}

const CommentDialog: React.FC<CommentDialogProps> = ({ postId, isOpen, onClose }) => {
  const { user } = useAuth();
  const { toast } = useToast();
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasNext, setHasNext] = useState(true);
  const [cursorTime, setCursorTime] = useState<string | undefined>(undefined);
  const [newComment, setNewComment] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState("");
  const [error, setError] = useState(false);
  
  const observerRef = useRef<IntersectionObserver | null>(null);
  const loadingRef = useRef<HTMLDivElement>(null);

  const loadComments = useCallback(async (reset = false) => {
    if ((loading && !reset) || (!hasNext && !reset) || error) return;
    
    setLoading(true);
    try {
      const currentCursor = reset ? undefined : cursorTime;
      const response: SliceCommentResponse = await getComments(postId, currentCursor);
      const newComments = response.data;
      
      setComments(prev => reset ? newComments : [...prev, ...newComments]);
      // @ts-ignore
      setHasNext(response.sliceInfo.hasNext);
      
      if (newComments.length > 0) {
        setCursorTime(newComments[newComments.length - 1].createdAt);
      }
      setError(false);
    } catch (error) {
      console.error('Failed to load comments:', error);
      setError(true);
      toast({
        title: "오류 발생",
        description: "댓글을 불러오는데 실패했습니다.",
        variant: "destructive"
      });
    } finally {
      setLoading(false);
    }
  }, [postId, loading, hasNext, cursorTime, toast, error]);

  useEffect(() => {
    if (isOpen) {
      setComments([]);
      setHasNext(true);
      setCursorTime(undefined);
      setError(false);
      loadComments(true);
    }
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen || loading || !hasNext || error) return;

    observerRef.current = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadComments();
        }
      },
      { threshold: 0.1 }
    );

    if (loadingRef.current) {
      observerRef.current.observe(loadingRef.current);
    }

    return () => {
      if (observerRef.current) {
        observerRef.current.disconnect();
      }
    };
  }, [isOpen, loading, hasNext, loadComments, error]);

  const handleSubmit = async () => {
    if (!newComment.trim()) return;
    
    if (!user) {
      toast({
        title: "로그인 필요",
        description: "댓글을 작성하려면 로그인이 필요합니다.",
        variant: "destructive"
      });
      return;
    }

    setSubmitting(true);
    try {
      await createComment(postId, newComment);
      setNewComment("");
      // Reload comments to show the new one
      setComments([]);
      setHasNext(true);
      setCursorTime(undefined);
      setError(false);
      loadComments(true);
      toast({
        title: "성공",
        description: "댓글이 작성되었습니다."
      });
    } catch (error) {
      toast({
        title: "오류 발생",
        description: "댓글 작성에 실패했습니다.",
        variant: "destructive"
      });
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (commentId: number) => {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    try {
      await deleteComment(commentId);
      setComments(prev => prev.filter(c => c.commentId !== commentId));
      toast({
        title: "삭제됨",
        description: "댓글이 삭제되었습니다."
      });
    } catch (error) {
      toast({
        title: "오류 발생",
        description: "댓글 삭제에 실패했습니다.",
        variant: "destructive"
      });
    }
  };

  const startEdit = (comment: Comment) => {
    setEditingId(comment.commentId);
    setEditContent(comment.comment);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditContent("");
  };

  const handleUpdate = async (commentId: number) => {
    if (!editContent.trim()) return;

    try {
      await updateComment(commentId, editContent);
      setComments(prev => prev.map(c => 
        c.commentId === commentId ? { ...c, comment: editContent } : c
      ));
      setEditingId(null);
      toast({
        title: "수정됨",
        description: "댓글이 수정되었습니다."
      });
    } catch (error) {
      toast({
        title: "오류 발생",
        description: "댓글 수정에 실패했습니다.",
        variant: "destructive"
      });
    }
  };

  function getFormattedDate(date: string) {
    const d = new Date(date);
    return `${d.getFullYear()}.${d.getMonth() + 1}.${d.getDate()} ${d.getHours()}:${d.getMinutes()}`;
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[500px] h-[80vh] flex flex-col p-0 gap-0">
        <DialogHeader className="p-4 border-b">
          <DialogTitle>댓글</DialogTitle>
        </DialogHeader>
        
        <ScrollArea className="flex-1 p-4">
          <div className="space-y-4">
            {comments.length === 0 && !loading && !error ? (
              <div className="text-center text-muted-foreground py-8">
                첫 번째 댓글을 남겨보세요!
              </div>
            ) : (
              comments.map((comment) => (
                <div key={comment.commentId} className="flex gap-3 group">
                  <div className="flex-1 space-y-1">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <span className="font-semibold text-sm">{comment.name}</span>
                        <span className="text-xs text-muted-foreground">
                          {getFormattedDate(comment.createdAt)}
                        </span>
                        {comment.isMyComment && (
                          <span className="text-[10px] bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 px-1.5 py-0.5 rounded-full font-medium">
                            내 댓글
                          </span>
                        )}
                      </div>
                      {comment.isMyComment && editingId !== comment.commentId && (
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="icon" className="h-6 w-6 opacity-0 group-hover:opacity-100 transition-opacity">
                              <MoreVertical className="h-3 w-3" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            <DropdownMenuItem onClick={() => startEdit(comment)}>
                              <Edit2 className="mr-2 h-3 w-3" />
                              수정
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={() => handleDelete(comment.commentId)} className="text-red-600">
                              <Trash2 className="mr-2 h-3 w-3" />
                              삭제
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      )}
                    </div>
                    
                    {editingId === comment.commentId ? (
                      <div className="space-y-2">
                        <Textarea
                          value={editContent}
                          onChange={(e) => setEditContent(e.target.value)}
                          className="min-h-[60px] text-sm"
                        />
                        <div className="flex justify-end gap-2">
                          <Button size="sm" variant="ghost" onClick={cancelEdit}>
                            <X className="h-3 w-3 mr-1" /> 취소
                          </Button>
                          <Button size="sm" onClick={() => handleUpdate(comment.commentId)}>
                            <Check className="h-3 w-3 mr-1" /> 저장
                          </Button>
                        </div>
                      </div>
                    ) : (
                      <p className="text-sm whitespace-pre-wrap break-words">{comment.comment}</p>
                    )}
                  </div>
                </div>
              ))
            )}
            
            {loading && (
              <div className="flex justify-center py-4">
                <Loader2 className="h-6 w-6 animate-spin text-green-600" />
              </div>
            )}

            {error && (
              <div className="text-center py-4">
                <p className="text-sm text-red-500 mb-2">댓글을 불러오는데 실패했습니다.</p>
                <Button 
                  variant="outline" 
                  size="sm" 
                  onClick={() => {
                    setError(false);
                    loadComments();
                  }}
                >
                  다시 시도
                </Button>
              </div>
            )}
            
            {hasNext && !loading && !error && <div ref={loadingRef} className="h-4" />}
          </div>
        </ScrollArea>

        <div className="p-4 border-t bg-background">
          {user ? (
            <div className="flex gap-2">
              <Textarea
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="댓글을 입력하세요..."
                className="min-h-[40px] max-h-[100px] resize-none"
              />
              <Button 
                onClick={handleSubmit} 
                disabled={submitting || !newComment.trim()}
                className="self-end"
              >
                {submitting ? <Loader2 className="h-4 w-4 animate-spin" /> : '등록'}
              </Button>
            </div>
          ) : (
            <div className="text-center text-sm text-muted-foreground py-2 bg-muted/50 rounded-md">
              댓글을 작성하려면 로그인이 필요합니다.
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default CommentDialog;