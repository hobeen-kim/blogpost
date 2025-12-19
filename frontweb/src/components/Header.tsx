import React, { useState } from 'react';
import { Search, Sun, Moon, Menu, User, LogOut } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet';
import { useAuth } from '@/contexts/AuthContext';
import { useTheme } from '@/contexts/ThemeContext';
import { useIsMobile } from '@/hooks/use-mobile';
import { cn } from '@/lib/utils';
import SearchDialog from '@/components/SearchDialog';

const Header: React.FC = () => {
  const { user, signInWithGoogle, signOut } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const isMobile = useIsMobile();
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleAuthAction = async () => {
    if (user) {
      await signOut();
    } else {
      await signInWithGoogle();
    }
  };

  const NavItems = () => (
    <>
      <Button
        variant="ghost"
        size="sm"
        onClick={() => setIsSearchOpen(true)}
        className="flex items-center gap-2 text-muted-foreground hover:text-foreground"
      >
        <Search className="h-4 w-4" />
        <span>검색</span>
      </Button>
      
      <Button
        variant="ghost"
        size="sm"
        onClick={toggleTheme}
        className="flex items-center gap-2 text-muted-foreground hover:text-foreground"
      >
        {theme === 'dark' ? (
          <>
            <Sun className="h-4 w-4" />
            <span>라이트 모드</span>
          </>
        ) : (
          <>
            <Moon className="h-4 w-4" />
            <span>다크 모드</span>
          </>
        )}
      </Button>

      <Button
        variant={user ? "ghost" : "default"}
        size="sm"
        onClick={handleAuthAction}
        className={cn(
          "flex items-center gap-2",
          user 
            ? "text-muted-foreground hover:text-foreground" 
            : "bg-green-600 hover:bg-green-700 text-white"
        )}
      >
        {user ? (
          <>
            <LogOut className="h-4 w-4" />
            <span>로그아웃</span>
          </>
        ) : (
          <>
            <User className="h-4 w-4" />
            <span>구글 로그인</span>
          </>
        )}
      </Button>
    </>
  );

  return (
    <>
      <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container mx-auto px-4 h-16 flex items-center justify-between max-w-6xl">
          {/* Logo */}
          <div className="flex items-center">
            <h1 className="text-2xl font-bold text-green-600 dark:text-green-400">
              devTag
            </h1>
          </div>

          {/* Desktop Navigation */}
          {!isMobile && (
            <nav className="flex items-center space-x-2">
              <NavItems />
            </nav>
          )}

          {/* Mobile Menu */}
          {isMobile && (
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
                        src={user.photoURL || ''}
                        alt={user.displayName || '사용자'}
                        className="w-10 h-10 rounded-full"
                      />
                      <div>
                        <p className="font-medium text-sm">{user.displayName}</p>
                        <p className="text-xs text-muted-foreground">{user.email}</p>
                      </div>
                    </div>
                  )}
                  <div className="flex flex-col space-y-2">
                    <NavItems />
                  </div>
                </div>
              </SheetContent>
            </Sheet>
          )}

          {/* User Avatar (Desktop) */}
          {!isMobile && user && (
            <div className="flex items-center space-x-3">
              <img
                src={user.photoURL || ''}
                alt={user.displayName || '사용자'}
                className="w-8 h-8 rounded-full border-2 border-green-200 dark:border-green-800"
              />
              <div className="hidden md:block">
                <p className="text-sm font-medium">{user.displayName}</p>
              </div>
            </div>
          )}
        </div>
      </header>

      <SearchDialog 
        isOpen={isSearchOpen} 
        onClose={() => setIsSearchOpen(false)} 
      />
    </>
  );
};

export default Header;