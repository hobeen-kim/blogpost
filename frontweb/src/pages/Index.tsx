import React, { useState } from 'react';
import Header from '@/components/Header';
import PostGrid from '@/components/PostGrid';

const Index = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [blogQuery, setBlogQuery] = useState('');

  const handleSearch = (query: string, blog?: string) => {
    setSearchQuery(query);
    if (blog !== undefined) {
      setBlogQuery(blog);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <Header onSearch={handleSearch} />
      <main className="container mx-auto px-4 py-8">
        <div className="flex gap-8">
          {/* Main Content */}
          <div className="flex-1">
            <PostGrid searchQuery={searchQuery} blogQuery={blogQuery} />
          </div>
        </div>
      </main>
    </div>
  );
};

export default Index;