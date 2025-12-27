import React from 'react';
import { Card, CardHeader, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useTheme } from "@/contexts/ThemeContext";
import { useAuth } from "@/contexts/AuthContext";
import { useToast } from "@/hooks/use-toast";
import { cn } from "@/lib/utils";
import {Share2, Heart} from "lucide-react";
import {like, removeLike} from '@/lib/api';

interface PostLikeCardProps {
  postId: string;
  title: string;
  description: string;
  author: string;
  pubDate?: string;
  tags: string[];
  thumbnail?: string;
  liked?: boolean;
  className?: string;
  url: string;
  onLikeChange?: (liked: boolean) => void;
}

const PostLikeCard: React.FC<PostLikeCardProps> = ({
  postId,
  title,
  description,
  author,
  pubDate,
  tags,
  thumbnail,
  liked = false,
  className,
  url,
  onLikeChange,
}) => {
  const { theme } = useTheme();
  const { user } = useAuth();
  const { toast } = useToast();
  const [isLiked, setLiked] = React.useState(liked);

  const handleLike = async () => {
    if (!user) {
      toast({
        title: "로그인이 필요합니다",
        description: "북마크를 추가하려면 먼저 로그인해주세요.",
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
        onLikeChange?.(false);
      } else {
        await like(postId);
        setLiked(true);
        toast({
          title: "좋아요",
          description: "게시글을 좋아합니다."
        });
        onLikeChange?.(true);
      }
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

  return (
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
            handleLike();
          }}
          className={cn(
            "absolute top-2 right-2 p-1.5 h-8 w-8 rounded-full backdrop-blur-sm transition-all duration-200",
              isLiked
                  ? "text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-500/10"
                  : "text-gray-500 hover:text-red-500 hover:bg-gray-100 dark:hover:bg-gray-700"
          )}
        >
          <Heart className={cn("h-4 w-4", isLiked && "fill-current")} />
        </Button>
      </div>

      <CardHeader className="p-4 pb-2 space-y-2">
        {/* 태그들 */}
        <div className="flex flex-wrap gap-1.5">
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

        {/* 하단 정보 */}
        <div className="flex items-center justify-between pt-3 border-t border-gray-200/50 dark:border-gray-700/50 mt-auto">
          <div className={cn(
            "flex items-center gap-2 text-xs",
            theme === 'dark' ? "text-gray-500" : "text-gray-500"
          )}>
            <span className="truncate max-w-[80px]">{author}</span>
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
  );
};

export default PostLikeCard;