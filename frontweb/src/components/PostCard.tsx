import React from 'react';
import { Card, CardHeader, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useTheme } from "@/contexts/ThemeContext";
import { useAuth } from "@/contexts/AuthContext";
import { useToast } from "@/hooks/use-toast";
import { cn } from "@/lib/utils";
import { Calendar, User, Heart, MessageCircle, Share2, Bookmark } from "lucide-react";
import {addBookmark, like, removeBookmark, removeLike} from '@/lib/api';

interface PostCardProps {
  postId: string;
  title: string;
  description: string;
  author: string;
  pubDate?: string;
  readTime?: string;
  tags: string[];
  thumbnail?: string;
  likes?: number;
  comments?: number;
  bookmarked?: boolean;
  bookmarkCount: number;
  liked?: boolean;
  likeCount: number;
  className?: string;
  url: string;
}

const PostCard: React.FC<PostCardProps> = ({
  postId,
  title,
  description,
  author,
  pubDate,
  readTime,
  tags,
  thumbnail,
  bookmarkCount,
  bookmarked,
  likeCount,
  liked,
  comments,
  className,
  url
}) => {
  const { theme } = useTheme();
  const { user } = useAuth();
  const { toast } = useToast();
  const [isBookmarked, setBookmarked] = React.useState(bookmarked);
  const [bookmarkNumber, setBookmarkNumber] = React.useState(bookmarkCount);
  const [isLiked, setLiked] = React.useState(liked);
  const [likeNumber, setLikeNumber] = React.useState(likeCount);

  const handleBookmark = async () => {
    if (!user) {
      toast({
        title: "로그인이 필요합니다",
        description: "북마크를 추가하려면 먼저 로그인해주세요.",
        variant: "destructive"
      });
      return;
    }

    try {
      if (isBookmarked) {
        await removeBookmark(postId);
        setBookmarked(false);
        toast({
          title: "북마크 제거",
          description: "북마크에서 제거했습니다."
        });
      } else {
        await addBookmark(postId);
        setBookmarked(true);
        toast({
          title: "북마크 추가",
          description: "북마크에 추가했습니다."
        });
      }

      setBookmarkNumber(prev => isBookmarked ? prev - 1 : prev + 1);
    } catch (error) {
      toast({
        title: "오류 발생",
        description: "북마크 처리 중 오류가 발생했습니다.",
        variant: "destructive"
      });
    }
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

  return (
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
            handleBookmark();
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
        {/* 태그들 */}
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

        {/* 제목 */}
        <h3 className={cn(
          "text-lg md:text-xl font-bold line-clamp-2 group-hover:text-green-600 transition-colors duration-200",
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
            <User className="h-4 w-4" />
            <span>{author}</span>
          </div>
          <div className="flex items-center gap-1">
            <Calendar className="h-4 w-4" />
            <span>{getFormattedDate(pubDate)}</span>
          </div>
          {/*<span className="text-green-600 font-medium">{readTime}</span>*/}
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
                  handleBookmark();

                }}
                className={cn(
                    "flex items-center gap-2 px-3 py-2 rounded-full transition-all duration-200",
                    isBookmarked
                        ? "text-green-500 hover:text-green-600 hover:bg-red-50 dark:hover:bg-red-500/10"
                        : "text-gray-500 hover:text-green-500 hover:bg-gray-100 dark:hover:bg-gray-700"
                )}
            >
              <Bookmark className={cn("h-4 w-4", isBookmarked && "fill-current")} />
              <span className="text-sm font-medium">{bookmarkNumber}</span>
            </Button>

            {/* 댓글 버튼 */}
            <Button
              variant="ghost"
              size="sm"
              className="flex items-center gap-2 px-3 py-2 rounded-full text-gray-500 hover:text-blue-500 hover:bg-gray-100 dark:hover:bg-gray-700 transition-all duration-200"
            >
              <MessageCircle className="h-4 w-4" />
              <span className="text-sm font-medium">{comments}</span>
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
  );
};

export default PostCard;