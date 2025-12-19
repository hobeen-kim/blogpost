import React, { useState, useEffect } from "react";
import { Search, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { cn } from "@/lib/utils";
import { useTheme } from "@/contexts/ThemeContext";
import {Post, Response} from '@/types/post';

interface SearchDialogProps {
  isOpen: boolean;
  onClose: () => void;
}

const SearchDialog: React.FC<SearchDialogProps> = ({ isOpen, onClose }) => {
  const { theme } = useTheme();
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState<Post[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [recentSearches, setRecentSearches] = useState<string[]>([]);

  useEffect(() => {
    const saved = localStorage.getItem("recentSearches");
    if (saved) {
      setRecentSearches(JSON.parse(saved));
    }
  }, []);

  useEffect(() => {
    if (searchQuery.trim()) {
      setIsSearching(true);
      fetchSearch(searchQuery)
          .then(res =>{
              console.log(res)
              setSearchResults(res.data)
          })
          .finally(
              () => {
                setIsSearching(false);
              }
      )
    } else {
      setSearchResults([]);
      setIsSearching(false);
    }
  }, [searchQuery]);

  const fetchSearch = async (query: string): Promise<Response[]> => {
    const response = await fetch(`https://blogtag-api.hobeenkim.com/posts/search?q=${query}`);
    return await response.json();
  }

  const handleSearch = (query: string) => {
    if (query.trim()) {
      const newRecentSearches = [query, ...recentSearches.filter(s => s !== query)].slice(0, 5);
      setRecentSearches(newRecentSearches);
      localStorage.setItem("recentSearches", JSON.stringify(newRecentSearches));
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSearch(searchQuery);
    }
  };

  const clearRecentSearches = () => {
    setRecentSearches([]);
    localStorage.removeItem("recentSearches");
  };

  const handleClose = () => {
    setSearchQuery("");
    setSearchResults([]);
    onClose();
  };

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className={cn(
        "max-w-2xl w-full mx-auto p-0 gap-0",
        theme === "dark" ? "bg-gray-900 border-gray-700" : "bg-white border-gray-200"
      )}>
        <DialogHeader className="p-6 pb-4">
          <DialogTitle className="sr-only">검색</DialogTitle>
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <Input
              type="text"
              placeholder="포스트 검색..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyPress={handleKeyPress}
              className={cn(
                "pl-10 pr-10 h-12 text-lg border-2 focus:ring-2 focus:ring-green-500",
                theme === "dark" 
                  ? "bg-gray-800 border-gray-600 text-white placeholder-gray-400" 
                  : "bg-white border-gray-300 text-gray-900 placeholder-gray-500"
              )}
              autoFocus
            />
            {searchQuery && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setSearchQuery("")}
                className="absolute right-2 top-1/2 transform -translate-y-1/2 p-1 h-8 w-8"
              >
                <X className="w-4 h-4" />
              </Button>
            )}
          </div>
        </DialogHeader>

        <div className={cn(
          "max-h-96 overflow-y-auto",
          theme === "dark" ? "bg-gray-900" : "bg-white"
        )}>
          {!searchQuery && recentSearches.length > 0 && (
            <div className="p-6 pt-0">
              <div className="flex items-center justify-between mb-3">
                <h3 className={cn(
                  "text-sm font-medium",
                  theme === "dark" ? "text-gray-300" : "text-gray-600"
                )}>
                  최근 검색어
                </h3>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={clearRecentSearches}
                  className="text-xs text-gray-500 hover:text-gray-700"
                >
                  전체 삭제
                </Button>
              </div>
              <div className="flex flex-wrap gap-2">
                {recentSearches.map((search, index) => (
                  <Button
                    key={index}
                    variant="outline"
                    size="sm"
                    onClick={() => setSearchQuery(search)}
                    className={cn(
                      "text-sm",
                      theme === "dark" 
                        ? "border-gray-600 text-gray-300 hover:bg-gray-800" 
                        : "border-gray-300 text-gray-600 hover:bg-gray-50"
                    )}
                  >
                    {search}
                  </Button>
                ))}
              </div>
            </div>
          )}

          {isSearching && (
            <div className="p-6 pt-0">
              <div className="flex items-center justify-center py-8">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-500"></div>
              </div>
            </div>
          )}

          {searchQuery && !isSearching && searchResults.length === 0 && (
            <div className="p-6 pt-0">
              <div className="text-center py-8">
                <Search className={cn(
                  "w-12 h-12 mx-auto mb-4",
                  theme === "dark" ? "text-gray-600" : "text-gray-400"
                )} />
                <p className={cn(
                  "text-lg font-medium mb-2",
                  theme === "dark" ? "text-gray-300" : "text-gray-600"
                )}>
                  검색 결과가 없습니다
                </p>
                <p className={cn(
                  "text-sm",
                  theme === "dark" ? "text-gray-500" : "text-gray-500"
                )}>
                  다른 키워드로 검색해보세요
                </p>
              </div>
            </div>
          )}

          {searchResults.length > 0 && (
            <div className="p-6 pt-0">
              <h3 className={cn(
                "text-sm font-medium mb-4",
                theme === "dark" ? "text-gray-300" : "text-gray-600"
              )}>
                검색 결과 ({searchResults.length}개)
              </h3>
              <div className="space-y-3">
                {searchResults.map((post) => (
                  <div
                    key={post.id}
                    className={cn(
                      "p-4 rounded-lg border cursor-pointer transition-colors hover:border-green-500",
                      theme === "dark" 
                        ? "border-gray-700 hover:bg-gray-800" 
                        : "border-gray-200 hover:bg-gray-50"
                    )}
                    onClick={() => {
                      handleSearch(searchQuery);
                      handleClose();
                    }}
                  >
                    <h4 className={cn(
                      "font-medium mb-2 line-clamp-1",
                      theme === "dark" ? "text-white" : "text-gray-900"
                    )}>
                      {post.title}
                    </h4>
                    <p className={cn(
                      "text-sm mb-3 line-clamp-2",
                      theme === "dark" ? "text-gray-400" : "text-gray-600"
                    )}>
                      {post.description}
                    </p>
                    <div className="flex items-center justify-between text-xs">
                      <div className="flex items-center space-x-2">
                        <span className={cn(
                          theme === "dark" ? "text-gray-500" : "text-gray-500"
                        )}>
                          {post.source}
                        </span>
                        <span className={cn(
                          theme === "dark" ? "text-gray-600" : "text-gray-400"
                        )}>
                          •
                        </span>
                        <span className={cn(
                          theme === "dark" ? "text-gray-500" : "text-gray-500"
                        )}>
                          {post.readTime}
                        </span>
                      </div>
                      <div className="flex flex-wrap gap-1">
                        {post.tags.slice(0, 2).map((tag) => (
                          <span
                            key={tag}
                            className={cn(
                              "px-2 py-1 rounded text-xs",
                              theme === "dark" 
                                ? "bg-gray-800 text-gray-400" 
                                : "bg-gray-100 text-gray-600"
                            )}
                          >
                            {tag}
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default SearchDialog;