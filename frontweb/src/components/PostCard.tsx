import React, { useState, useEffect } from 'react';
import { Card, CardHeader, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useTheme } from "@/contexts/ThemeContext";
import { useAuth } from "@/contexts/AuthContext";
import { useToast } from "@/hooks/use-toast";
import { cn } from "@/lib/utils";
import { Calendar, User, Heart, MessageCircle, Share2, Bookmark, Plus, Folder } from "lucide-react";
import { like, removeLike, getBookmarkGroupsWithPost, addBookmarkToGroup, createBookmarkGroup, removeBookmarkFromGroup } from '@/lib/api';
import CommentDialog from '@/components/CommentDialog';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";

interface PostCardProps {
  postId: string;
  title: string;
  description: string;
  source: string;
  pubDate?: string;
  readTime?: string;
  tags: string[];
  thumbnail?: string;
  bookmarked: boolean;
  bookmarkCount: number;
  liked: boolean;
  likeCount: number;
  commented: boolean;
  commentCount: number;
  className?: string;
  url: string;
  metadata: object;
}

interface BookmarkGroupWithPost extends BookmarkGroup {
  hasPost: boolean;
}

interface BookmarkGroup {
  bookmarkGroupId: number;
  name: string;
}

const PostCard: React.FC<PostCardProps> = ({
  postId,
  title,
  description,
  source,
  pubDate,
  readTime,
  tags,
  thumbnail,
  bookmarkCount,
  bookmarked,
  likeCount,
  liked,
  commented,
  commentCount,
  className,
  url,
  metadata,
}) => {
  const { theme } = useTheme();
  const { user } = useAuth();
  const { toast } = useToast();
  const [isBookmarked, setBookmarked] = React.useState(bookmarked);
  const [bookmarkNumber, setBookmarkNumber] = React.useState(bookmarkCount);
  const [isLiked, setLiked] = React.useState(liked);
  const [likeNumber, setLikeNumber] = React.useState(likeCount);
  const [isCommented, setCommented] = React.useState(commented);
  const [commentNumber, setCommentNumber] = React.useState(commentCount);
  const [isCommentOpen, setIsCommentOpen] = useState(false);
  const [logoError, setLogoError] = useState(false);

  // Bookmark Group State
  const [isBookmarkDialogOpen, setIsBookmarkDialogOpen] = useState(false);
  const [bookmarkGroups, setBookmarkGroups] = useState<BookmarkGroupWithPost[]>([]);
  const [initialSelectedGroups, setInitialSelectedGroups] = useState<number[]>([]);
  const [selectedGroups, setSelectedGroups] = useState<number[]>([]);
  const [newGroupName, setNewGroupName] = useState('');
  const [isCreatingGroup, setIsCreatingGroup] = useState(false);

  const loadBookmarkGroups = async () => {
    try {
      const response = await getBookmarkGroupsWithPost(postId);
      const groupsWithPost: BookmarkGroupWithPost[] = response.data;
      setBookmarkGroups(groupsWithPost);
      const initiallySelected = groupsWithPost.filter(g => g.hasPost).map(g => g.bookmarkGroupId);
      setSelectedGroups(initiallySelected);
      setInitialSelectedGroups(initiallySelected);
    } catch (error) {
      console.error('Failed to load bookmark groups:', error);
    }
  };

  const handleBookmarkClick = async () => {
    if (!user) {
      toast({
        title: "로그인이 필요합니다",
        description: "북마크를 추가하려면 먼저 로그인해주세요.",
        variant: "destructive"
      });
      return;
    }
    await loadBookmarkGroups();
    setIsBookmarkDialogOpen(true);
  };

  const handleSaveBookmarks = async () => {
    try {
      const toAdd = selectedGroups.filter(id => !initialSelectedGroups.includes(id));
      const toRemove = initialSelectedGroups.filter(id => !selectedGroups.includes(id));

      await Promise.all([
        ...toAdd.map(groupId => addBookmarkToGroup(groupId, postId)),
        ...toRemove.map(groupId => removeBookmarkFromGroup(groupId, postId))
      ]);

      if (selectedGroups.length > 0 && !isBookmarked) {
        setBookmarked(true);
        setBookmarkNumber(prev => prev + 1);
      } else if (selectedGroups.length === 0 && isBookmarked) {
        setBookmarked(false);
        setBookmarkNumber(prev => Math.max(0, prev - 1));
      }

      toast({ title: "북마크 저장", description: "북마크 그룹이 업데이트되었습니다." });
      setIsBookmarkDialogOpen(false);
    } catch (error) {
      console.error(error);
      toast({
        title: "오류 발생",
        description: "북마크 저장 중 오류가 발생했습니다.",
        variant: "destructive"
      });
    }
  };

  const handleCreateGroup = async () => {
    if (!newGroupName.trim()) return;

    try {
      await createBookmarkGroup(newGroupName);
      await loadBookmarkGroups();
      setNewGroupName('');
      setIsCreatingGroup(false);
    } catch (error) {
      toast({
        title: "그룹 생성 실패",
        description: "북마크 그룹 생성 중 오류가 발생했습니다.",
        variant: "destructive"
      });
    }
  };

  const toggleGroupSelection = (groupId: number) => {
    setSelectedGroups(prev =>
      prev.includes(groupId)
        ? prev.filter(id => id !== groupId)
        : [...prev, groupId]
    );
  };

  const handleLike = async () => {
    if (!user) {
      toast({
        title: "로그인이 필요합니다",
        description: "좋아요를 누르려면 먼저 로그인해주세요.",
        variant: "destructive"
      });
      return;
    }

    try {
      if (isLiked) {
        await removeLike(postId);
        setLiked(false);
        toast({
          title: "좋아요 취소",
          description: "좋아요를 취소했습니다."
        });
      } else {
        await like(postId);
        setLiked(true);
        toast({
          title: "좋아요",
          description: "게시글을 좋아합니다."
        });
      }

      setLikeNumber(prev => isLiked ? prev - 1 : prev + 1);
    } catch (error) {
      toast({
        title: "오류 발생",
        description: "좋아요 처리 중 오류가 발생했습니다.",
        variant: "destructive"
      });
    }
  };

  const handleShare = async () => {
    try {
      if (navigator.share) {
        await navigator.share({
          title: title,
          text: description,
          url: window.location.href
        });
      } else {
        await navigator.clipboard.writeText(window.location.href);
        toast({
          title: "링크 복사됨",
          description: "포스트 링크가 클립보드에 복사되었습니다."
        });
      }
    } catch (error) {
      toast({
        title: "공유 실패",
        description: "포스트 공유에 실패했습니다.",
        variant: "destructive"
      });
    }
  };

  function getFormattedDate(date: string)  {
    const d = new Date(date);

    const y = d.getFullYear();
    const m = d.getMonth() + 1;
    const day = d.getDate();

    return `${y}년 ${m}월 ${day}일`
  }

  const handleImageError = (e: React.SyntheticEvent<HTMLImageElement, Event>) => {
    e.currentTarget.src = '/placeholder.svg';
  };

  const handleCardClick = () => {
    window.open(url, '_blank', 'noopener,noreferrer');
  };

  const handleCommentAdded = () => {
    setCommented(true);
    setCommentNumber(prev => prev + 1);
  };

  const handleCommentDeleted = () => {
    setCommentNumber(prev => Math.max(0, prev - 1));
    if (commentNumber <= 1) {
      setCommented(false);
    }
  };

  return (
    <>
      <Card
        onClick={handleCardClick}
        className={cn(
        "group cursor-pointer transition-all duration-300 hover:shadow-lg border-0 shadow-sm",
        "w-full max-w-sm md:max-w-lg lg:max-w-2xl mx-auto",
        theme === 'dark'
          ? "bg-gray-800/50 hover:bg-gray-800/70 border-gray-700/50"
          : "bg-white hover:bg-gray-50/80 border-gray-200/50",
        className
      )}>
        {/* 포스트 이미지 */}
        <div className="relative overflow-hidden rounded-t-lg">
          <img
            src={thumbnail || '/placeholder.svg'}
            alt={title}
            onError={handleImageError}
            className="w-full h-48 md:h-56 lg:h-64 object-cover transition-transform duration-300 group-hover:scale-105"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300" />

          {/* 북마크 버튼 */}
          <Button
            variant="ghost"
            size="sm"
            onClick={(e) => {
              e.stopPropagation();
              handleBookmarkClick();
            }}
            className={cn(
              "absolute top-3 right-3 p-2 rounded-full backdrop-blur-sm transition-all duration-200",
              isBookmarked
                ? "bg-green-500/90 text-white hover:bg-green-600/90"
                : "bg-white/80 text-gray-700 hover:bg-white/90"
            )}
          >
            <Bookmark className={cn("h-4 w-4", isBookmarked && "fill-current")} />
          </Button>
        </div>

        <CardHeader className="pb-3">
          {/* 제목 */}
          <h3 className={cn(
            "text-lg md:text-xl font-bold group-hover:text-green-600 transition-colors duration-200",
            theme === 'dark' ? "text-white" : "text-gray-900"
          )}>
            {title}
          </h3>

          {/* 작성자 및 날짜 정보 */}
          <div className={cn(
            "flex items-center gap-4 text-sm",
            theme === 'dark' ? "text-gray-400" : "text-gray-600"
          )}>
            <div className="flex items-center gap-1">
              {!logoError ? (
                <img
                  src={`/logo/${source}.png`}
                  alt={source}
                  className="h-4 w-4 rounded-sm"
                  onError={() => setLogoError(true)}
                />
              ) : (
                <User className="h-4 w-4" />
              )}
              <span>{metadata['ko'] ?? source}</span>
            </div>
            <div className="flex items-center gap-1">
              <Calendar className="h-4 w-4" />
              <span>{getFormattedDate(pubDate)}</span>
            </div>
          </div>
        </CardHeader>

        <CardContent className="pt-0">
          {/* 포스트 요약 */}
          <p className={cn(
            "text-sm md:text-base line-clamp-3 mb-4 leading-relaxed",
            theme === 'dark' ? "text-gray-300" : "text-gray-700"
          )}>
            {description}
          </p>

          {/* 태그들 */}
          {tags && tags.length > 0 && (
            <div className="flex flex-wrap gap-2 mb-3">
              {tags.map((tag, index) => (
                <span
                  key={index}
                  className={cn(
                    "px-2 py-1 text-xs font-medium rounded-full transition-colors",
                    theme === 'dark'
                      ? "bg-green-500/20 text-green-400 hover:bg-green-500/30"
                      : "bg-green-100 text-green-700 hover:bg-green-200"
                  )}
                >
                  #{tag}
                </span>
              ))}
            </div>
          )}

          {/* 액션 버튼들 */}
          <div className="flex items-center justify-between pt-3 border-t border-gray-200/50 dark:border-gray-700/50">
            <div className="flex items-center gap-4">

              {/* 좋아요 버튼 */}
              <Button
                variant="ghost"
                size="sm"
                onClick={(e) => {
                  e.stopPropagation();
                  handleLike()
                }}
                className={cn(
                  "flex items-center gap-2 px-3 py-2 rounded-full transition-all duration-200",
                  isLiked
                    ? "text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-500/10"
                    : "text-gray-500 hover:text-red-500 hover:bg-gray-100 dark:hover:bg-gray-700"
                )}
              >
                <Heart className={cn("h-4 w-4", isLiked && "fill-current")} />
                <span className="text-sm font-medium">{likeNumber}</span>
              </Button>

              {/* 북마크 버튼 */}
              <Button
                  variant="ghost"
                  size="sm"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleBookmarkClick();

                  }}
                  className={cn(
                      "flex items-center gap-2 px-3 py-2 rounded-full transition-all duration-200",
                      isBookmarked
                          ? "text-green-500 hover:text-green-600 hover:bg-green-50 dark:hover:bg-green-500/10"
                          : "text-gray-500 hover:text-green-500 hover:bg-green-100 dark:hover:bg-gray-700"
                  )}
              >
                <Bookmark className={cn("h-4 w-4", isBookmarked && "fill-current")} />
                <span className="text-sm font-medium">{bookmarkNumber}</span>
              </Button>

              {/* 댓글 버튼 */}
              <Button
                variant="ghost"
                size="sm"
                onClick={(e) => {
                  e.stopPropagation();
                  setIsCommentOpen(true);
                }}
                className={cn("flex items-center gap-2 px-3 py-2 rounded-full duration-200",
                    isCommented
                      ? "text-blue-500 hover:text-blue-600 hover:bg-gray-100 dark:hover:bg-gray-700 transition-all duration-200"
                        : "text-gray-500 hover:text-blue-500 hover:bg-gray-100 dark:hover:bg-gray-700 transition-all duration-200"
                )}
              >
                <MessageCircle className={cn("h-4 w-4", isCommented && "fill-current")}/>
                <span className="text-sm font-medium">{commentNumber}</span>
              </Button>
            </div>

            {/* 공유 버튼 */}
            <Button
              variant="ghost"
              size="sm"
              onClick={(e) => {
                e.stopPropagation();
                handleShare();
              }}
              className="flex items-center gap-2 px-3 py-2 rounded-full text-gray-500 hover:text-green-600 hover:bg-gray-100 dark:hover:bg-gray-700 transition-all duration-200"
            >
              <Share2 className="h-4 w-4" />
              <span className="text-sm font-medium">공유</span>
            </Button>
          </div>
        </CardContent>
      </Card>

      <CommentDialog
        postId={postId}
        isOpen={isCommentOpen}
        onClose={() => setIsCommentOpen(false)}
        onCommentAdded={handleCommentAdded}
        onCommentDeleted={handleCommentDeleted}
      />

      {/* Bookmark Group Dialog */}
      <Dialog open={isBookmarkDialogOpen} onOpenChange={setIsBookmarkDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>북마크 그룹 선택</DialogTitle>
          </DialogHeader>

          <div className="py-4 space-y-4">
            <div className="max-h-[200px] overflow-y-auto space-y-2">
              {bookmarkGroups.map((group) => (
                <div key={group.bookmarkGroupId} className="flex items-center space-x-2 p-2 hover:bg-muted rounded-md">
                  <Checkbox
                    id={`group-${group.bookmarkGroupId}`}
                    checked={selectedGroups.includes(group.bookmarkGroupId)}
                    onCheckedChange={() => toggleGroupSelection(group.bookmarkGroupId)}
                  />
                  <Label
                    htmlFor={`group-${group.bookmarkGroupId}`}
                    className="flex-1 cursor-pointer flex items-center gap-2"
                  >
                    <Folder className="h-4 w-4 text-muted-foreground" />
                    {group.name}
                  </Label>
                </div>
              ))}
            </div>

            {isCreatingGroup ? (
              <div className="flex items-center gap-2 pt-2 border-t">
                <Input
                  placeholder="새 그룹 이름"
                  value={newGroupName}
                  onChange={(e) => setNewGroupName(e.target.value)}
                  className="h-8"
                />
                <Button size="sm" onClick={handleCreateGroup}>추가</Button>
                <Button size="sm" variant="ghost" onClick={() => setIsCreatingGroup(false)}>취소</Button>
              </div>
            ) : (
              <Button
                variant="ghost"
                size="sm"
                className="w-full justify-start text-muted-foreground"
                onClick={() => setIsCreatingGroup(true)}
              >
                <Plus className="h-4 w-4 mr-2" />
                새 그룹 만들기
              </Button>
            )}
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => setIsBookmarkDialogOpen(false)}>취소</Button>
            <Button onClick={handleSaveBookmarks}>저장</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default PostCard;