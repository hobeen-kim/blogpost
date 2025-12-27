import React, { useState, useEffect, useCallback, useRef } from 'react';
import PostCard from '@/components/PostCard';
import { Loader2 } from 'lucide-react';
import { Post, PagedResponse } from '@/types/post';
import {getPosts} from "@/lib/api.ts";

const PostGrid: React.FC = () => {
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [page, setPage] = useState(0);
  const observerRef = useRef<IntersectionObserver | null>(null);
  const loadingRef = useRef<HTMLDivElement>(null);

  // Load posts function
  const loadPosts = useCallback(async (pageNum: number) => {
    if (loading) return;
    
    setLoading(true);
    
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    const response  = await getPosts(pageNum);

    const newPosts = response.data
    const pageInfo = response.pageInfo

    console.log(pageInfo)

    if (pageNum === 0) {
      setPosts(newPosts);
    } else {
      setPosts(prev => [...prev, ...newPosts]);
    }
    
    // Simulate end of data after 5 pages
    if (!pageInfo.hasNext) {
      setHasMore(false);
    }
    
    setLoading(false);
  }, [loading]);

  // Initial load
  useEffect(() => {
    loadPosts(0);
  }, []);

  // Intersection Observer for infinite scroll
  useEffect(() => {
    if (loading || !hasMore) return;

    observerRef.current = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          const nextPage = page + 1;
          setPage(nextPage);
          loadPosts(nextPage);
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
  }, [loading, hasMore, page, loadPosts]);

  return (
    <div className="w-full max-w-6xl mx-auto px-4 py-6">
      {/* Posts Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {posts.map((post) => (
          <div key={post.postId} className="w-full">
            <PostCard
              postId={post.postId}
              title={post.title}
              description={post.description}
              author={post.source}
              pubDate={post.pubDate}
              readTime={post.readTime}
              tags={post.tags}
              thumbnail={post.thumbnail}
              url={post.url}
              bookmarked={post.bookmarked}
              bookmarkCount={post.bookmarkCount}
              liked={post.liked}
              likeCount={post.likeCount}
              commented={post.commented}
              commentCount={post.commentCount}
              // onLike={() => handlePostLike(post.id)}
              // onComment={() => handlePostComment(post.id)}
            />
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