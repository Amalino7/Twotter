export type PostResponse = {
  id: string;
  content: string;
  userId: string;
  userHandle: string;
  createdAt: string;
  updatedAt: string;
  imageUrl?: string;
  userDisplayName: string;
  hasLiked: boolean;
  likesCount: number;
  commentsCount: number;
  repostsCount: number;
  postType: string;
};
