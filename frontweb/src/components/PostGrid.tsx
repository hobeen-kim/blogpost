import React, { useState, useEffect, useCallback, useRef } from 'react';
import PostCard from '@/components/PostCard';
import PostHorizontalCard from '@/components/PostHorizontalCard';
import { Loader2, LayoutGrid, List } from 'lucide-react';
import { Post } from '@/types/post';
import { getPosts } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

interface PostGridProps {
  searchQuery: string;
  blogQuery?: string;
}

const PostGrid: React.FC<PostGridProps> = ({ searchQuery, blogQuery }) => {
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [page, setPage] = useState(0);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const observerRef = useRef<IntersectionObserver | null>(null);
  const loadingRef = useRef<HTMLDivElement>(null);

  const loadPosts = useCallback(async (pageNum: number, query: string, blog?: string) => {
    if (loading) return;
    
    setLoading(true);
    
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      const response = await getPosts(pageNum, query, blog);
      const newPosts = response.data;
      const pageInfo = response.pageInfo;

      setPosts(prev => pageNum === 0 ? newPosts : [...prev, ...newPosts]);
      setHasMore(pageInfo.hasNext);
    } catch (error) {
      console.error("Failed to load posts:", error);
    } finally {
      setLoading(false);
    }
  }, [loading]);

  useEffect(() => {
    setPosts([]);
    setPage(0);
    setHasMore(true);
    loadPosts(0, searchQuery, blogQuery);
  }, [searchQuery, blogQuery]);

  useEffect(() => {
    if (loading || !hasMore) return;

    observerRef.current = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          const nextPage = page + 1;
          setPage(nextPage);
          loadPosts(nextPage, searchQuery, blogQuery);
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
  }, [loading, hasMore, page, loadPosts, searchQuery, blogQuery]);

  return (
    <div className="w-full max-w-6xl mx-auto px-4 py-6">
      {/* View Mode Toggle */}
      <div className="hidden md:flex justify-end mb-4">
        <div className="inline-flex items-center rounded-md bg-muted p-1">
          <Button
            variant={viewMode === 'grid' ? 'default' : 'ghost'}
            size="sm"
            onClick={() => setViewMode('grid')}
            className={cn("h-8 px-3", viewMode === 'grid' && "shadow-sm bg-background text-foreground")}
          >
            <LayoutGrid className="h-4 w-4" />
          </Button>
          <Button
            variant={viewMode === 'list' ? 'default' : 'ghost'}
            size="sm"
            onClick={() => setViewMode('list')}
            className={cn("h-8 px-3", viewMode === 'list' && "shadow-sm bg-background text-foreground")}
          >
            <List className="h-4 w-4" />
          </Button>
        </div>
      </div>

      {/* Posts Grid */}
      <div className={cn(
        "grid gap-6",
        "grid-cols-1", // Mobile default
        "md:grid-cols-1", // Desktop list view
        viewMode === 'grid' && "md:grid-cols-2" // Desktop grid view
      )}>
        {posts.map((post) => (
          <div key={post.postId} className="w-full">
            {/* Mobile View (always PostCard) */}
            <div className="block md:hidden">
              <PostCard {...post} />
            </div>
            {/* Desktop View (conditional) */}
            <div className="hidden md:block">
              {viewMode === 'list' ? (
                <PostHorizontalCard {...post} />
              ) : (
                <PostCard {...post} />
              )}
            </div>
          </div>
        ))}
      </div>

      {/* Loading indicator */}
      {loading && (
        <div className="flex justify-center items-center py-8">
          <Loader2 className="h-8 w-8 animate-spin text-green-600" />
          <span className="ml-2 text-gray-600 dark:text-gray-400">
            포스트를 불러오는 중...
          </span>
        </div>
      )}

      {/* Intersection observer target */}
      {hasMore && !loading && (
        <div ref={loadingRef} className="h-10 flex justify-center items-center">
          <div className="text-sm text-gray-500 dark:text-gray-400">
            더 많은 포스트 불러오기...
          </div>
        </div>
      )}

      {/* End of posts message */}
      {!hasMore && posts.length > 0 && (
        <div className="text-center py-8">
          <div className="text-gray-500 dark:text-gray-400">
            모든 포스트를 확인했습니다.
          </div>
        </div>
      )}

      {/* Empty state */}
      {!loading && posts.length === 0 && (
        <div className="text-center py-12">
          <div className="text-gray-500 dark:text-gray-400 text-lg">
            표시할 포스트가 없습니다.
          </div>
        </div>
      )}
    </div>
  );
};

export default PostGrid;