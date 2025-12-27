import React, { useEffect, useState, useRef, useCallback } from 'react';
import { Routes, Route, Link, useLocation, useNavigate } from 'react-router-dom';
import { User, Bookmark, Heart, LogOut, Loader2 } from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import Header from '@/components/Header';
import { getBookmarks, getLikes } from '@/lib/api';
import PostCard from '@/components/PostCard';
import PostBookmarkCard from '@/components/PostBookmarkCard';
import { Post } from '@/types/post';
import { PostBookmark, SliceBookmarkResponse } from '@/types/bookmark';

const UserProfile = () => {
  const { user, signOut } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await signOut();
    navigate('/');
  };

  if (!user) return null;

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">사용자 정보</h2>
      <div className="flex items-center space-x-4">
        <img
          src={user.avatar || ''}
          alt={user.name}
          className="w-20 h-20 rounded-full border-2 border-gray-200 dark:border-gray-700"
        />
        <div>
          <p className="text-xl font-semibold">{user.name}</p>
          <p className="text-muted-foreground">{user.email}</p>
        </div>
      </div>
      
      <div className="pt-6 border-t">
        <Button 
          variant="destructive" 
          onClick={handleLogout}
          className="flex items-center gap-2"
        >
          <LogOut className="h-4 w-4" />
          로그아웃
        </Button>
      </div>
    </div>
  );
};

const Bookmarks = () => {
  const [bookmarks, setBookmarks] = useState<PostBookmark[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasNext, setHasNext] = useState(true);
  const [cursorTime, setCursorTime] = useState<string | undefined>(undefined);
  const observerRef = useRef<IntersectionObserver | null>(null);
  const loadingRef = useRef<HTMLDivElement>(null);

  const loadBookmarks = useCallback(async () => {
    if (loading || !hasNext) return;
    
    setLoading(true);
    try {
      const response: SliceBookmarkResponse = await getBookmarks(cursorTime);
      const newBookmarks = response.data;
      
      setBookmarks(prev => [...prev, ...newBookmarks]);
      setHasNext(response.sliceInfo.hasNext);
      
      if (newBookmarks.length > 0) {
        setCursorTime(newBookmarks[newBookmarks.length - 1].bookmarkTime);
      }
    } catch (error) {
      console.error('Failed to load bookmarks:', error);
    } finally {
      setLoading(false);
    }
  }, [loading, hasNext, cursorTime]);

  useEffect(() => {
    loadBookmarks();
  }, []); // Initial load

  useEffect(() => {
    if (loading || !hasNext) return;

    observerRef.current = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadBookmarks();
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
  }, [loading, hasNext, loadBookmarks]);

  const handleBookmarkChange = (postId: string, isBookmarked: boolean) => {
    if (!isBookmarked) {
      setBookmarks(prev => prev.filter(b => b.postId !== postId));
    }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">북마크 목록</h2>
      {bookmarks.length === 0 && !loading ? (
        <p className="text-muted-foreground">저장된 북마크가 없습니다.</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {bookmarks.map((bookmark) => (
            <PostBookmarkCard
              key={bookmark.postId}
              postId={bookmark.postId}
              title={bookmark.title}
              description={bookmark.description}
              author={bookmark.source}
              pubDate={bookmark.pubDate}
              tags={bookmark.tags}
              thumbnail={bookmark.thumbnail}
              isBookmarked={true}
              url={bookmark.url}
              onBookmarkChange={(isBookmarked) => handleBookmarkChange(bookmark.postId, isBookmarked)}
            />
          ))}
        </div>
      )}
      
      {loading && (
        <div className="flex justify-center items-center py-4">
          <Loader2 className="h-6 w-6 animate-spin text-green-600" />
        </div>
      )}
      
      {hasNext && !loading && <div ref={loadingRef} className="h-4" />}
    </div>
  );
};

const Likes = () => {
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadData = async () => {
      try {
        const response = await getLikes();
        setPosts(response.data || []);
      } catch (err) {
        setError('데이터를 불러오는 중 오류가 발생했습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-green-600" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12 text-red-500">
        {error}
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">좋아요 목록</h2>
      {posts.length === 0 ? (
        <p className="text-muted-foreground">좋아요한 게시글이 없습니다.</p>
      ) : (
        <div className="grid grid-cols-1 gap-6">
          {posts.map((post) => (
            <PostCard
              key={post.id}
              id={post.id}
              title={post.title}
              description={post.description}
              author={post.source}
              pubDate={post.pubDate}
              readTime={post.readTime}
              tags={post.tags}
              thumbnail={post.thumbnail}
              likes={post.likes}
              comments={post.comments}
              url={post.url}
              isBookmarked={post.bookmarked}
            />
          ))}
        </div>
      )}
    </div>
  );
};

const MyPage = () => {
  const location = useLocation();
  const currentPath = location.pathname;

  const navItems = [
    { path: '/mypage', label: '사용자 정보', icon: User },
    { path: '/mypage/bookmarks', label: '북마크 목록', icon: Bookmark },
    { path: '/mypage/likes', label: '좋아요 목록', icon: Heart },
  ];

  return (
    <div className="min-h-screen bg-background">
      <Header />
      <div className="container mx-auto px-4 py-8 max-w-6xl">
        <div className="flex flex-col md:flex-row gap-8">
          {/* Sidebar Navigation */}
          <aside className="w-full md:w-64 shrink-0">
            <nav className="flex flex-col space-y-1">
              {navItems.map((item) => {
                const Icon = item.icon;
                const isActive = currentPath === item.path || (item.path !== '/mypage' && currentPath.startsWith(item.path));
                
                return (
                  <Link
                    key={item.path}
                    to={item.path}
                    className={cn(
                      "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors",
                      isActive 
                        ? "bg-green-50 text-green-700 dark:bg-green-900/20 dark:text-green-400" 
                        : "text-muted-foreground hover:bg-muted hover:text-foreground"
                    )}
                  >
                    <Icon className="h-4 w-4" />
                    {item.label}
                  </Link>
                );
              })}
            </nav>
          </aside>

          {/* Main Content */}
          <main className="flex-1 min-h-[500px] bg-card rounded-xl border p-6 shadow-sm">
            <Routes>
              <Route path="/" element={<UserProfile />} />
              <Route path="/bookmarks" element={<Bookmarks />} />
              <Route path="/likes" element={<Likes />} />
            </Routes>
          </main>
        </div>
      </div>
    </div>
  );
};

export default MyPage;