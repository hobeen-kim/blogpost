import React, { useEffect, useState, useRef, useCallback } from 'react';
import { Routes, Route, Link, useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import { User, Bookmark, Heart, LogOut, Loader2, Plus, Trash2, Edit2, Folder, MoreVertical, ChevronDown, ChevronRight } from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogFooter } from '@/components/ui/dialog';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { cn } from '@/lib/utils';
import Header from '@/components/Header';
import { getBookmarks, getLikes, getBookmarkGroups, createBookmarkGroup, updateBookmarkGroup, deleteBookmarkGroup } from '@/lib/api';
import PostBookmarkCard from '@/components/PostBookmarkCard';
import { PostBookmark, PostLike, SliceBookmarkResponse, SliceLikeResponse } from '@/types/bookmarkLike.ts';
import PostLikeCard from "@/components/PostLikeCard.tsx";
import { useToast } from "@/hooks/use-toast";
import { Collapsible, CollapsibleContent } from "@/components/ui/collapsible";


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

interface BookmarkGroup {
  bookmarkGroupId: number;
  name: string;
}

const Bookmarks = () => {
  const [bookmarks, setBookmarks] = useState<PostBookmark[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasNext, setHasNext] = useState(true);
  const [cursorTime, setCursorTime] = useState<string | undefined>(undefined);
  const [searchParams] = useSearchParams();
  const groupId = searchParams.get('groupId');
  const selectedGroupId = groupId ? parseInt(groupId) : null;

  const observerRef = useRef<IntersectionObserver | null>(null);
  const loadingRef = useRef<HTMLDivElement>(null);

  const loadBookmarks = useCallback(async (reset = false) => {
    if (loading || (!hasNext && !reset)) return;

    setLoading(true);
    try {
      const currentCursor = reset ? undefined : cursorTime;
      const response: SliceBookmarkResponse = await getBookmarks(currentCursor, selectedGroupId ?? undefined);
      const newBookmarks = response.data;

      setBookmarks(prev => reset ? newBookmarks : [...prev, ...newBookmarks]);
      setHasNext(response.sliceInfo.hasNext);

      if (newBookmarks.length > 0) {
        setCursorTime(newBookmarks[newBookmarks.length - 1].bookmarkedTime);
      } else if (reset) {
        setCursorTime(undefined);
      }
    } catch (error) {
      console.error('Failed to load bookmarks:', error);
    } finally {
      setLoading(false);
    }
  }, [loading, hasNext, cursorTime, selectedGroupId]);

  useEffect(() => {
    setBookmarks([]);
    setCursorTime(undefined);
    setHasNext(true);
    setLoading(false);

    (async () => {
      setLoading(true);
      try {
        const response: SliceBookmarkResponse = await getBookmarks(undefined, selectedGroupId ?? undefined);
        setBookmarks(response.data);
        setHasNext(response.sliceInfo.hasNext);
        if (response.data.length > 0) {
          setCursorTime(response.data[response.data.length - 1].bookmarkedTime);
        }
      } catch (error) {
        console.error('Failed to load bookmarks:', error);
      } finally {
        setLoading(false);
      }
    })();
  }, [selectedGroupId]);

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
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">북마크 목록</h2>
      </div>

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
              source={bookmark.source}
              pubDate={bookmark.pubDate}
              tags={bookmark.tags}
              thumbnail={bookmark.thumbnail}
              isBookmarked={true}
              url={bookmark.url}
              metadata={bookmark.metadata}
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
  const [likes, setLikes] = useState<PostLike[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasNext, setHasNext] = useState(true);
  const [cursorTime, setCursorTime] = useState<string | undefined>(undefined);
  const observerRef = useRef<IntersectionObserver | null>(null);
  const loadingRef = useRef<HTMLDivElement>(null);

  const loadLikes = useCallback(async () => {
    if (loading || !hasNext) return;

    setLoading(true);
    try {
      const response: SliceLikeResponse = await getLikes(cursorTime);
      const newLikes = response.data;

      setLikes(prev => [...prev, ...newLikes]);
      setHasNext(response.sliceInfo.hasNext);

      if (newLikes.length > 0) {
        setCursorTime(newLikes[newLikes.length - 1].likedTime);
      }
    } catch (error) {
      console.error('Failed to load bookmarks:', error);
    } finally {
      setLoading(false);
    }
  }, [loading, hasNext, cursorTime]);

  useEffect(() => {
    loadLikes();
  }, []); // Initial load

  useEffect(() => {
    if (loading || !hasNext) return;

    observerRef.current = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting) {
            loadLikes();
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
  }, [loading, hasNext, loadLikes]);

  const handleLikeChange = (postId: string, isLiked: boolean) => {
    if (!isLiked) {
      setLikes(prev => prev.filter(b => b.postId !== postId));
    }
  };

  return (
      <div className="space-y-6">
        <h2 className="text-2xl font-bold">좋아요 목록</h2>
        {likes.length === 0 && !loading ? (
            <p className="text-muted-foreground">저장된 좋아요가 없습니다.</p>
        ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {likes.map((like) => (
                  <PostLikeCard
                      key={like.postId}
                      postId={like.postId}
                      title={like.title}
                      description={like.description}
                      source={like.source}
                      pubDate={like.pubDate}
                      tags={like.tags}
                      thumbnail={like.thumbnail}
                      liked={true}
                      url={like.url}
                      metadata={like.metadata}
                      onLikeChange={(liked) => handleLikeChange(like.postId, liked)}
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

const MyPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const currentPath = location.pathname;
  const { toast } = useToast();

  const [groups, setGroups] = useState<BookmarkGroup[]>([]);
  const [isBookmarksExpanded, setIsBookmarksExpanded] = useState(false);
  const [newGroupName, setNewGroupName] = useState('');
  const [isCreateGroupOpen, setIsCreateGroupOpen] = useState(false);
  const [editingGroup, setEditingGroup] = useState<BookmarkGroup | null>(null);
  const [isEditGroupOpen, setIsEditGroupOpen] = useState(false);

  const loadGroups = async () => {
    try {
      const response = await getBookmarkGroups();
      setGroups(response.data);
    } catch (error) {
      console.error('Failed to load bookmark groups:', error);
    }
  };

  useEffect(() => {
    loadGroups();
  }, []);

  const handleCreateGroup = async () => {
    if (!newGroupName.trim()) return;

    try {
      await createBookmarkGroup(newGroupName);
      setNewGroupName('');
      setIsCreateGroupOpen(false);
      loadGroups();
    } catch (error: any) {
      toast({
        title: "그룹 생성 실패",
        description: error.message || "북마크 그룹 생성 중 오류가 발생했습니다.",
        variant: "destructive"
      });
    }
  };

  const handleUpdateGroup = async () => {
    if (!editingGroup || !editingGroup.name.trim()) return;

    try {
      await updateBookmarkGroup(editingGroup.bookmarkGroupId, editingGroup.name);
      setEditingGroup(null);
      setIsEditGroupOpen(false);
      loadGroups();
    } catch (error: any) {
      toast({
        title: "그룹 수정 실패",
        description: error.message || "북마크 그룹 수정 중 오류가 발생했습니다.",
        variant: "destructive"
      });
    }
  };

  const handleDeleteGroup = async (groupId: number) => {
    if (!window.confirm('그룹을 삭제하시겠습니까? 그룹 내의 모든 북마크가 삭제됩니다.')) return;

    try {
      await deleteBookmarkGroup(groupId);
      const currentGroupId = searchParams.get('groupId');
      if (currentGroupId && parseInt(currentGroupId) === groupId) {
        navigate('/mypage/bookmarks');
      }
      loadGroups();
    } catch (error: any) {
      toast({
        title: "그룹 삭제 실패",
        description: error.message || "북마크 그룹 삭제 중 오류가 발생했습니다.",
        variant: "destructive"
      });
    }
  };

  const navItems = [
    { path: '/mypage', label: '사용자 정보', icon: User },
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
              <Link
                to="/mypage"
                className={cn(
                  "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors",
                  currentPath === '/mypage'
                    ? "bg-green-50 text-green-700 dark:bg-green-900/20 dark:text-green-400"
                    : "text-muted-foreground hover:bg-muted hover:text-foreground"
                )}
                onClick={() => {
                  setIsBookmarksExpanded(false);
                }}
              >
                <User className="h-4 w-4" />
                사용자 정보
              </Link>

              {/* Bookmarks Section */}
              <Collapsible
                open={isBookmarksExpanded}
                onOpenChange={setIsBookmarksExpanded}
                className="space-y-1"
              >
                <div
                  className={cn(
                    "flex items-center justify-between px-4 py-3 rounded-lg text-sm font-medium transition-colors cursor-pointer",
                    currentPath === '/mypage/bookmarks' && !searchParams.get('groupId')
                      ? "bg-green-50 text-green-700 dark:bg-green-900/20 dark:text-green-400"
                      : "text-muted-foreground hover:bg-muted hover:text-foreground"
                  )}
                  onClick={() => {
                    if (currentPath !== '/mypage/bookmarks' || searchParams.get('groupId')) {
                      navigate('/mypage/bookmarks');
                    }
                    setIsBookmarksExpanded(!isBookmarksExpanded);
                  }}
                >
                  <div className="flex items-center gap-3">
                    <Bookmark className="h-4 w-4" />
                    북마크 목록
                  </div>
                  {isBookmarksExpanded ? <ChevronDown className="h-4 w-4" /> : <ChevronRight className="h-4 w-4" />}
                </div>

                <CollapsibleContent
                      className="
                        overflow-hidden
                        data-[state=open]:animate-collapsible-down
                        data-[state=closed]:animate-collapsible-up
                      "
                  >
                    <div className="pl-8 space-y-1">
                      <div className="flex items-center justify-between px-4 py-2">
                        <span className="text-xs font-medium text-muted-foreground">그룹 목록</span>
                        <Dialog open={isCreateGroupOpen} onOpenChange={setIsCreateGroupOpen}>
                          <DialogTrigger asChild>
                            <Button variant="ghost" size="icon" className="h-6 w-6">
                              <Plus className="h-3 w-3"/>
                            </Button>
                          </DialogTrigger>
                          <DialogContent>
                            <DialogHeader>
                              <DialogTitle>새 북마크 그룹 만들기</DialogTitle>
                            </DialogHeader>
                            <div className="py-4">
                              <Input
                                  placeholder="그룹 이름"
                                  value={newGroupName}
                                  onChange={(e) => setNewGroupName(e.target.value)}
                              />
                            </div>
                            <DialogFooter>
                              <Button onClick={handleCreateGroup}>생성</Button>
                            </DialogFooter>
                          </DialogContent>
                        </Dialog>
                      </div>

                      <Link
                          to="/mypage/bookmarks"
                          className={cn(
                              "flex items-center gap-3 px-4 py-2 rounded-lg text-sm transition-colors",
                              currentPath === '/mypage/bookmarks' && !searchParams.get('groupId')
                                  ? "text-green-700 font-medium dark:text-green-400"
                                  : "text-muted-foreground hover:text-foreground"
                          )}
                      >
                        <Folder className="h-3 w-3"/>
                        전체
                      </Link>

                      {groups.map((group) => (
                          <div key={group.bookmarkGroupId} className="group/item relative flex items-center">
                            <Link
                                to={`/mypage/bookmarks?groupId=${group.bookmarkGroupId}`}
                                className={cn(
                                    "flex-1 flex items-center gap-3 px-4 py-2 rounded-lg text-sm transition-colors",
                                    searchParams.get('groupId') === group.bookmarkGroupId.toString()
                                        ? "text-green-700 font-medium dark:text-green-400"
                                        : "text-muted-foreground hover:text-foreground"
                                )}
                            >
                              <Folder className="h-3 w-3"/>
                              <span className="truncate max-w-[120px]">{group.name}</span>
                            </Link>
                            {group.name !== '기본' && (
                                <DropdownMenu>
                                  <DropdownMenuTrigger asChild>
                                    <Button
                                        variant="ghost"
                                        size="icon"
                                        className="h-6 w-6 absolute right-2 opacity-0 group-hover/item:opacity-100 transition-opacity"
                                    >
                                      <MoreVertical className="h-3 w-3"/>
                                    </Button>
                                  </DropdownMenuTrigger>
                                  <DropdownMenuContent align="end">
                                    <DropdownMenuItem onClick={() => {
                                      setEditingGroup(group);
                                      setIsEditGroupOpen(true);
                                    }}>
                                      <Edit2 className="h-4 w-4 mr-2"/>
                                      이름 수정
                                    </DropdownMenuItem>
                                    <DropdownMenuItem
                                        className="text-destructive focus:text-destructive"
                                        onClick={() => handleDeleteGroup(group.bookmarkGroupId)}
                                    >
                                      <Trash2 className="h-4 w-4 mr-2"/>
                                      삭제
                                    </DropdownMenuItem>
                                  </DropdownMenuContent>
                                </DropdownMenu>
                            )}
                          </div>
                      ))}
                    </div>
                  </CollapsibleContent>
              </Collapsible>

              <Link
                to="/mypage/likes"
                className={cn(
                  "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors",
                  currentPath === '/mypage/likes'
                    ? "bg-green-50 text-green-700 dark:bg-green-900/20 dark:text-green-400"
                    : "text-muted-foreground hover:bg-muted hover:text-foreground"
                )}
                onClick={() => {
                  setIsBookmarksExpanded(false);
                }}
              >
                <Heart className="h-4 w-4" />
                좋아요 목록
              </Link>
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

      {/* Edit Group Dialog */}
      <Dialog open={isEditGroupOpen} onOpenChange={setIsEditGroupOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>북마크 그룹 수정</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            <Input
              placeholder="그룹 이름"
              value={editingGroup?.name || ''}
              onChange={(e) => setEditingGroup(prev => prev ? { ...prev, name: e.target.value } : null)}
            />
          </div>
          <DialogFooter>
            <Button onClick={handleUpdateGroup}>수정</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default MyPage;