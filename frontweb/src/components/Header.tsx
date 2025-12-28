import React, { useState, useEffect } from 'react';
import { Search, Sun, Moon, Menu, Filter, User } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet';
import { useAuth } from '@/contexts/AuthContext';
import { useTheme } from '@/contexts/ThemeContext';
import { useIsMobile } from '@/hooks/use-mobile';
import { cn } from '@/lib/utils';
import { Link } from 'react-router-dom';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import { Checkbox } from "@/components/ui/checkbox"
import { Label } from "@/components/ui/label"
import { getSources } from '@/lib/api';

const GoogleIcon = (props: React.SVGProps<SVGSVGElement>) => (
  <svg
    {...props}
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 48 48"
    width="16px"
    height="16px"
  >
    <path
      fill="#FFC107"
      d="M43.611,20.083H42V20H24v8h11.303c-1.649,4.657-6.08,8-11.303,8c-6.627,0-12-5.373-12-12c0-6.627,5.373-12,12-12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C12.955,4,4,12.955,4,24c0,11.045,8.955,20,20,20c11.045,0,20-8.955,20-20C44,22.659,43.862,21.35,43.611,20.083z"
    />
    <path
      fill="#FF3D00"
      d="M6.306,14.691l6.571,4.819C14.655,15.108,18.961,12,24,12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C16.318,4,9.656,8.337,6.306,14.691z"
    />
    <path
      fill="#4CAF50"
      d="M24,44c5.166,0,9.86-1.977,13.409-5.192l-6.19-5.238C29.211,35.091,26.715,36,24,36c-5.222,0-9.619-3.317-11.283-7.946l-6.522,5.025C9.505,39.556,16.227,44,24,44z"
    />
    <path
      fill="#1976D2"
      d="M43.611,20.083H42V20H24v8h11.303c-0.792,2.237-2.231,4.166-4.087,5.574l6.19,5.238C42.012,36.45,44,30.638,44,24C44,22.659,43.862,21.35,43.611,20.083z"
    />
  </svg>
);

interface HeaderProps {
  onSearch?: (query: string, blog?: string) => void;
}

interface Source {
  source: string;
  count: number;
  metadataCache: {
    ko: string;
  };
}

const Header: React.FC<HeaderProps> = ({ onSearch }) => {
  const { user, signInWithGoogle } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const isMobile = useIsMobile();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [searchInputValue, setSearchInputValue] = useState('');
  const [sources, setSources] = useState<Source[]>([]);
  const [selectedSources, setSelectedSources] = useState<string[]>([]);

  useEffect(() => {
    const fetchSources = async () => {
      try {
        const response = await getSources();
        setSources(response.data);
      } catch (error) {
        console.error("Failed to fetch sources:", error);
      }
    };
    fetchSources();
  }, []);

  const handleLogin = async () => {
    await signInWithGoogle();
  };

  const handleSearch = () => {
    const blogQuery = selectedSources.join(',');
    onSearch?.(searchInputValue, blogQuery);
  };

  const toggleSource = (source: string) => {
    setSelectedSources(prev => {
      const newSelection = prev.includes(source)
        ? prev.filter(s => s !== source)
        : [...prev, source];
      
      // Trigger search immediately when filter changes if there's a search term or just to filter
      const blogQuery = newSelection.join(',');
      onSearch?.(searchInputValue, blogQuery);
      
      return newSelection;
    });
  };

  const NavItems = () => (
    <>
      <Button
        variant="ghost"
        size="sm"
        onClick={toggleTheme}
        className="flex items-center gap-2 text-muted-foreground hover:text-foreground"
      >
        {theme === 'dark' ? (
          <>
            <Sun className="h-4 w-4" />
            <span className="md:hidden">라이트 모드</span>
          </>
        ) : (
          <>
            <Moon className="h-4 w-4" />
            <span className="md:hidden">다크 모드</span>
          </>
        )}
      </Button>

      {user ? (
        <Link to="/mypage">
          <Button
            variant="ghost"
            size="sm"
            className="flex items-center gap-2 text-muted-foreground hover:text-foreground"
          >
            <img
              src={user.avatar || ''}
              alt={user.name || '사용자'}
              className="w-6 h-6 rounded-full border border-border"
            />
            <span className="max-w-[100px] truncate md:hidden">{user.name}</span>
          </Button>
        </Link>
      ) : (
        <Button
          variant="ghost"
          size="sm"
          onClick={handleLogin}
          className="flex items-center gap-2 text-muted-foreground hover:text-foreground"
        >
          <GoogleIcon className="h-4 w-4" />
          <span>구글 로그인</span>
        </Button>
      )}
    </>
  );

  const FilterContent = () => (
    <div className="w-auto min-w-[300px] max-w-[600px] p-4">
      <div className="space-y-4">
        <div>
          <h4 className="font-medium mb-2">블로그 출처</h4>
          <div className="grid grid-cols-5 gap-1 max-h-[300px] overflow-y-auto p-1">
            {sources.map((source) => (
              <div 
                key={source.source} 
                className="flex flex-col items-center space-y-1 p-1 rounded-md hover:bg-muted/50 cursor-pointer"
                onMouseDown={(e) => {
                  // Prevent focus change which might cause scroll jump
                  e.preventDefault();
                }}
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  toggleSource(source.source);
                }}
              >
                <div className="relative pointer-events-none">
                  <div className={cn(
                    "w-10 h-10 flex items-center justify-center rounded-full overflow-hidden transition-all",
                    selectedSources.includes(source.source) ? "ring-2 ring-primary/20" : ""
                  )}>
                    <img 
                      src={`/logo/${source.source}.png`} 
                      alt={source.source}
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.currentTarget.style.display = 'none';
                        e.currentTarget.nextElementSibling?.classList.remove('hidden');
                      }}
                    />
                    <div className="w-full h-full bg-muted flex items-center justify-center hidden">
                      <User className="w-5 h-5" />
                    </div>
                  </div>
                  {selectedSources.includes(source.source) && (
                    <div className="absolute -top-1 -right-1 w-4 h-4 bg-primary rounded-full border-2 border-background flex items-center justify-center">
                      <div className="w-1.5 h-1.5 bg-white rounded-full" />
                    </div>
                  )}
                </div>
                <span className="text-xs text-center truncate w-full font-medium px-1 pointer-events-none">
                  {source.metadataCache?.ko || source.source}
                </span>
              </div>
            ))}
          </div>
        </div>
        
        <div className="pt-2 border-t">
          <h4 className="font-medium mb-2">태그</h4>
          <div className="text-sm text-muted-foreground p-2 text-center bg-muted/30 rounded">
            태그 필터 준비 중...
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container mx-auto px-4 h-16 flex items-center justify-between max-w-6xl gap-4">
        {/* Logo */}
        <div className="flex items-center">
          <Link to="/">
            <h1 className="text-2xl font-bold text-green-600 dark:text-green-400">
              devTag
            </h1>
          </Link>
        </div>

        {/* Search Bar (Desktop) */}
        <div className="hidden md:flex flex-1 max-w-md items-center space-x-2">
          <Input 
            type="search" 
            placeholder="검색어를 입력하세요..." 
            className="h-9"
            value={searchInputValue}
            onChange={(e) => setSearchInputValue(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          />
          <Popover>
            <PopoverTrigger asChild>
              <Button variant="outline" size="sm" className="h-9 px-3">
                <Filter className="h-4 w-4 mr-2" />
                필터
                {selectedSources.length > 0 && (
                  <span className="ml-1 rounded-full bg-primary w-5 h-5 text-[10px] flex items-center justify-center text-primary-foreground">
                    {selectedSources.length}
                  </span>
                )}
              </Button>
            </PopoverTrigger>
            <PopoverContent align="end" className="p-0 w-auto">
              <FilterContent />
            </PopoverContent>
          </Popover>
          <Button type="submit" size="sm" className="h-9" onClick={handleSearch}>
            <Search className="h-4 w-4" />
          </Button>
        </div>

        {/* Right Navigation */}
        <div className="flex items-center">
          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center space-x-1">
            <NavItems />
          </nav>

          {/* Mobile Menu */}
          <div className="md:hidden">
            <Sheet open={isMobileMenuOpen} onOpenChange={setIsMobileMenuOpen}>
              <SheetTrigger asChild>
                <Button variant="ghost" size="sm">
                  <Menu className="h-5 w-5" />
                </Button>
              </SheetTrigger>
              <SheetContent side="right" className="w-64">
                <div className="flex flex-col space-y-4 mt-8">
                  {user && (
                    <div className="flex items-center space-x-3 pb-4 border-b">
                      <img
                        src={user.avatar || ''}
                        alt={user.name || '사용자'}
                        className="w-10 h-10 rounded-full"
                      />
                      <div>
                        <p className="font-medium text-sm">{user.name}</p>
                        <p className="text-xs text-muted-foreground">{user.email}</p>
                      </div>
                    </div>
                  )}
                  <div className="flex flex-col space-y-2">
                    <div className="flex items-center space-x-2 p-2">
                      <Input 
                        type="search" 
                        placeholder="검색..." 
                        className="h-9"
                        value={searchInputValue}
                        onChange={(e) => setSearchInputValue(e.target.value)}
                        onKeyDown={(e) => {
                          if (e.key === 'Enter') {
                            handleSearch();
                            setIsMobileMenuOpen(false);
                          }
                        }}
                      />
                      <Button type="submit" size="icon" className="h-9 w-9" onClick={() => {
                        handleSearch();
                        setIsMobileMenuOpen(false);
                      }}>
                        <Search className="h-4 w-4" />
                      </Button>
                    </div>
                    
                    {/* Mobile Filter */}
                    <div className="px-2">
                      <Popover>
                        <PopoverTrigger asChild>
                          <Button variant="outline" size="sm" className="w-full justify-start">
                            <Filter className="h-4 w-4 mr-2" />
                            필터
                            {selectedSources.length > 0 && (
                              <span className="ml-auto rounded-full bg-primary w-5 h-5 text-[10px] flex items-center justify-center text-primary-foreground">
                                {selectedSources.length}
                              </span>
                            )}
                          </Button>
                        </PopoverTrigger>
                        <PopoverContent align="start" className="p-0 w-auto max-w-[300px]">
                          <div className="w-full p-4">
                            <div className="space-y-4">
                              <div>
                                <h4 className="font-medium mb-2">블로그 출처</h4>
                                <div className="grid grid-cols-3 gap-1 max-h-[300px] overflow-y-auto p-1">
                                  {sources.map((source) => (
                                    <div 
                                      key={source.source} 
                                      className="flex flex-col items-center space-y-1 p-1 rounded-md hover:bg-muted/50 cursor-pointer"
                                      onMouseDown={(e) => {
                                        // Prevent focus change which might cause scroll jump
                                        e.preventDefault();
                                      }}
                                      onClick={(e) => {
                                        e.preventDefault();
                                        e.stopPropagation();
                                        toggleSource(source.source);
                                      }}
                                    >
                                      <div className="relative pointer-events-none">
                                        <div className={cn(
                                          "w-8 h-8 flex items-center justify-center rounded-full overflow-hidden transition-all",
                                          selectedSources.includes(source.source) ? "ring-2 ring-primary/20" : ""
                                        )}>
                                          <img 
                                            src={`/logo/${source.source}.png`} 
                                            alt={source.source}
                                            className="w-full h-full object-cover"
                                            onError={(e) => {
                                              e.currentTarget.style.display = 'none';
                                              e.currentTarget.nextElementSibling?.classList.remove('hidden');
                                            }}
                                          />
                                          <div className="w-full h-full bg-muted flex items-center justify-center hidden">
                                            <User className="w-4 h-4" />
                                          </div>
                                        </div>
                                        {selectedSources.includes(source.source) && (
                                          <div className="absolute -top-1 -right-1 w-3 h-3 bg-primary rounded-full border-2 border-background flex items-center justify-center">
                                            <div className="w-1 h-1 bg-white rounded-full" />
                                          </div>
                                        )}
                                      </div>
                                      <span className="text-[10px] text-center truncate w-full font-medium px-1 pointer-events-none">
                                        {source.metadataCache?.ko || source.source}
                                      </span>
                                    </div>
                                  ))}
                                </div>
                              </div>
                              
                              <div className="pt-2 border-t">
                                <h4 className="font-medium mb-2">태그</h4>
                                <div className="text-sm text-muted-foreground p-2 text-center bg-muted/30 rounded">
                                  태그 필터 준비 중...
                                </div>
                              </div>
                            </div>
                          </div>
                        </PopoverContent>
                      </Popover>
                    </div>

                    <NavItems />
                  </div>
                </div>
              </SheetContent>
            </Sheet>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;