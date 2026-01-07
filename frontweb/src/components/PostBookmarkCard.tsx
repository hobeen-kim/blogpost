import React, {useState} from 'react';
import { Card, CardHeader, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useTheme } from "@/contexts/ThemeContext";
import { useAuth } from "@/contexts/AuthContext";
import { useToast } from "@/hooks/use-toast";
import { cn } from "@/lib/utils";
import {User, Share2, Bookmark, Folder} from "lucide-react";
import {
  addBookmarkToGroup,
  getBookmarkGroupsWithPost,
  removeBookmarkFromGroup
} from '@/lib/api';
import {useSearchParams} from "react-router-dom";
import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle} from "@/components/ui/dialog.tsx";
import {Checkbox} from "@/components/ui/checkbox.tsx";
import {Label} from "@/components/ui/label.tsx";

interface BookmarkGroupWithPost extends BookmarkGroup {
  hasPost: boolean;
}

interface BookmarkGroup {
  bookmarkGroupId: number;
  name: string;
}

interface PostBookmarkCardProps {
  postId: string;
  title: string;
  description: string;
  source: string;
  pubDate?: string;
  tags: string[];
  thumbnail?: string;
  isBookmarked?: boolean;
  className?: string;
  url: string;
  onBookmarkChange?: (isBookmarked: boolean) => void;
  metadata: object,
}

const PostBookmarkCard: React.FC<PostBookmarkCardProps> = ({
  postId,
  title,
  description,
  source,
  pubDate,
  tags,
  thumbnail,
  isBookmarked = false,
  className,
  url,
  onBookmarkChange,
  metadata,
}) => {
  const { theme } = useTheme();
  const { user } = useAuth();
  const { toast } = useToast();
  const [bookmarked, setBookmarked] = React.useState(isBookmarked);
  const [logoError, setLogoError] = useState(false);
  const [searchParams] = useSearchParams();
  const groupId = searchParams.get("groupId")
  const [isBookmarkDialogOpen, setIsBookmarkDialogOpen] = useState(false);
  const [bookmarkGroups, setBookmarkGroups] = useState<BookmarkGroupWithPost[]>([]);
  const [initialSelectedGroups, setInitialSelectedGroups] = useState<number[]>([]);
  const [selectedGroups, setSelectedGroups] = useState<number[]>([]);

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

  function getFormattedDate(date?: string)  {
    if (!date) return '';
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

  const toggleGroupSelection = (groupId: number) => {
    setSelectedGroups(prev =>
        prev.includes(groupId)
            ? prev.filter(id => id !== groupId)
            : [...prev, groupId]
    );
  };

  const handleSaveBookmarks = async () => {
    try {
      const toAdd = selectedGroups.filter(id => !initialSelectedGroups.includes(id));
      const toRemove = initialSelectedGroups.filter(id => !selectedGroups.includes(id));

      await Promise.all([
        ...toAdd.map(groupId => addBookmarkToGroup(groupId, postId)),
        ...toRemove.map(groupId => removeBookmarkFromGroup(groupId, postId))
      ]);

      if(groupId && toRemove.includes(Number(groupId))) {
        onBookmarkChange?.(false);
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

  return (
    <>
    <Card
      onClick={handleCardClick}
      className={cn(
      "group cursor-pointer transition-all duration-300 hover:shadow-lg border-0 shadow-sm flex flex-col h-full",
      theme === 'dark'
        ? "bg-gray-800/50 hover:bg-gray-800/70 border-gray-700/50"
        : "bg-white hover:bg-gray-50/80 border-gray-200/50",
      className
    )}>
      {/* 포스트 이미지 */}
      <div className="relative overflow-hidden rounded-t-lg h-40 shrink-0">
        <img
          src={thumbnail || '/placeholder.svg'}
          alt={title}
          onError={handleImageError}
          className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
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
            "absolute top-2 right-2 p-1.5 h-8 w-8 rounded-full backdrop-blur-sm transition-all duration-200",
            bookmarked
              ? "bg-green-500/90 text-white hover:bg-green-600/90"
              : "bg-white/80 text-gray-700 hover:bg-white/90"
          )}
        >
          <Bookmark className={cn("h-4 w-4", bookmarked && "fill-current")} />
        </Button>
      </div>

      <CardHeader className="p-4 pb-2 space-y-2">
        {/* 제목 */}
        <h3 className={cn(
          "text-base font-bold line-clamp-2 group-hover:text-green-600 transition-colors duration-200 leading-tight",
          theme === 'dark' ? "text-white" : "text-gray-900"
        )}>
          {title}
        </h3>
      </CardHeader>

      <CardContent className="p-4 pt-0 flex-1 flex flex-col justify-between">
        {/* 포스트 요약 */}
        <p className={cn(
          "text-xs line-clamp-2 mb-3 leading-relaxed",
          theme === 'dark' ? "text-gray-400" : "text-gray-600"
        )}>
          {description}
        </p>

        {/* 태그들 */}
        <div className="flex flex-wrap gap-1.5 mb-3">
          {tags.slice(0, 3).map((tag, index) => (
            <span
              key={index}
              className={cn(
                "px-1.5 py-0.5 text-[10px] font-medium rounded-full transition-colors",
                theme === 'dark'
                  ? "bg-green-500/20 text-green-400"
                  : "bg-green-100 text-green-700"
              )}
            >
              #{tag}
            </span>
          ))}
        </div>

        {/* 하단 정보 */}
        <div className="flex items-center justify-between pt-3 border-t border-gray-200/50 dark:border-gray-700/50 mt-auto">
          <div className={cn(
            "flex items-center gap-2 text-xs",
            theme === 'dark' ? "text-gray-500" : "text-gray-500"
          )}>
            {!logoError ? (
                <img
                    src={`/logo/${source}.png`}
                    alt={source}
                    className="h-3.5 w-3.5 rounded-sm"
                    onError={() => setLogoError(true)}
                />
            ) : (
                <User className="h-3.5 w-3.5" />
            )}
            <span className="truncate max-w-[80px]">{metadata['ko'] ?? source}</span>
            <span>•</span>
            <span>{getFormattedDate(pubDate)}</span>
          </div>

          {/* 공유 버튼 */}
          <Button
            variant="ghost"
            size="sm"
            onClick={(e) => {
              e.stopPropagation();
              handleShare();
            }}
            className="h-6 w-6 p-0 rounded-full text-gray-400 hover:text-green-600 hover:bg-transparent"
          >
            <Share2 className="h-3.5 w-3.5" />
          </Button>
        </div>
      </CardContent>
    </Card>
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

export default PostBookmarkCard;