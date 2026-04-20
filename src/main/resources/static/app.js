const { createApp } = Vue;

const categoryMap = {
    "传统技艺": "Traditional Craftsmanship",
    "传统戏曲": "Traditional Opera",
    "古建筑": "Historic Architecture",
    "民俗节庆": "Folk Rituals & Festivals"
};

const nicknameMap = {
    "平台管理员": "Platform Curator",
    "小组成员1": "Team Member 1"
};

const textMap = {
    "苏绣的针法之美": "The Needlework Elegance of Suzhou Embroidery",
    "苏绣是中国传统刺绣的重要代表，讲究针法细腻、设色雅致。": "Suzhou embroidery is one of the most celebrated traditions of Chinese needlework, known for refined stitching and graceful colour composition.",
    "这篇文章适合后面再补几张针法细节图。": "This article would become even stronger with a few close-up images of the stitching techniques.",
    "手工艺、织染、雕刻等": "Craft traditions, dyeing, weaving, carving, and studio practice.",
    "戏曲、曲艺、说唱艺术": "Opera, ballad performance, oral storytelling, and stage heritage.",
    "古城、古桥、古园林与历史建筑": "Ancient towns, bridges, gardens, and protected architecture.",
    "节日仪式、民间风俗与庆典": "Festival ritual, seasonal custom, and collective celebration."
};

const heritageMap = {
    "苏绣": "Suzhou Embroidery",
    "昆曲": "Kunqu Opera",
    "蓝染技艺": "Indigo Dyeing",
    "皮影戏": "Shadow Puppetry",
    "元宵灯会": "Lantern Festival Procession",
    "廊桥营造技艺": "Covered Bridge Carpentry",
    "泥塑": "Clay Figurine Sculpture"
};

const regionMap = {
    "江苏苏州": "Suzhou, Jiangsu",
    "陕西西安": "Xi'an, Shaanxi",
    "福建泉州": "Quanzhou, Fujian",
    "贵州黔东南": "Qiandongnan, Guizhou",
    "浙江温州": "Wenzhou, Zhejiang",
    "天津": "Tianjin"
};

const backendMessageMap = {
    "用户名已存在": "This username is already in use.",
    "用户不存在": "The user could not be found.",
    "账号已被禁用": "This account has been disabled.",
    "用户名或密码错误": "The username or password is incorrect.",
    "文章不存在": "The article could not be found.",
    "分类不存在": "The collection could not be found.",
    "分类名称已存在": "This collection name already exists.",
    "评论用户不能为空": "A valid comment author is required.",
    "评论内容不能为空": "Comment content cannot be empty.",
    "作者不能为空": "An author is required.",
    "分类不能为空": "Please choose a collection.",
    "标题不能为空": "A title is required.",
    "正文不能为空": "Story text cannot be empty.",
    "用户名不能为空": "Username cannot be empty.",
    "密码不能为空": "Password cannot be empty.",
    "昵称不能为空": "Display name cannot be empty.",
    "仅管理员可执行该操作": "Only administrators can perform this action.",
    "请先登录": "Please sign in again before continuing.",
    "当前文章状态不允许提交审核": "This article cannot be submitted for review right now.",
    "仅草稿或已驳回文章可编辑": "Only draft or rejected articles can be edited.",
    "仅待审核文章可执行审核操作": "Only pending review articles can be reviewed.",
    "驳回原因不能为空": "Please provide a rejection reason.",
    "仅已发布文章可归档": "Only published articles can be archived.",
    "仅已归档文章可恢复发布": "Only archived articles can be restored.",
    "不能修改管理员角色": "Administrator roles cannot be changed.",
    "不支持将用户设置为管理员": "Users cannot be promoted to administrator here.",
    "当前角色无需调整": "This user already has that role.",
    "仅支持在普通用户与贡献者之间调整角色": "Only USER and CONTRIBUTOR roles can be adjusted here.",
    "目标角色不能为空": "Please choose a target role.",
    "用户已启用": "User account activated successfully.",
    "用户已停用": "User account deactivated successfully.",
    "用户当前已启用": "This user account is already active.",
    "用户当前已停用": "This user account is already inactive.",
    "不能停用管理员账号": "Administrator accounts cannot be deactivated.",
    "不能启用管理员账号": "Administrator accounts cannot be activated here.",
    "不能停用当前登录管理员": "You cannot deactivate the administrator account that is currently signed in.",
    "当前角色无需申请贡献者权限": "Your current role does not require a contributor application.",
    "已有待处理的贡献者申请": "You already have a pending contributor application.",
    "申请不存在": "The application could not be found.",
    "仅待处理申请可审批": "Only pending applications can be reviewed.",
    "申请人当前角色不允许审批此申请": "This application can no longer be reviewed because the applicant role has changed."
};

const categoryDescriptionMap = {
    "传统技艺": "Material culture, workshop practice, motifs, and making processes.",
    "传统戏曲": "Performance traditions, costume, vocal lineages, and stage memory.",
    "古建筑": "Historic buildings, construction craft, spatial heritage, and preservation.",
    "民俗节庆": "Seasonal ritual, festivals, community gatherings, and oral custom."
};

const fallbackCategories = [
    { id: 1, name: "传统技艺", description: "Material culture, workshop practice, motifs, and making processes." },
    { id: 2, name: "传统戏曲", description: "Performance traditions, costume, vocal lineages, and stage memory." },
    { id: 3, name: "古建筑", description: "Historic buildings, construction craft, spatial heritage, and preservation." },
    { id: 4, name: "民俗节庆", description: "Seasonal ritual, festivals, community gatherings, and oral custom." }
];

const fallbackPostDetails = {
    101: {
        id: 101,
        title: "The Garden Stage Language of Kunqu",
        content: "This front-end demo article shows how a longer heritage essay can be displayed on the platform. It combines historical background, performance detail, and scene-setting notes that a student team can later connect to real backend data.",
        coverImageUrl: "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=1200&q=80",
        heritageName: "昆曲",
        region: "江苏苏州",
        status: "PUBLISHED",
        authorName: "平台管理员",
        categoryName: "传统戏曲",
        likeCount: 34,
        favoriteCount: 19,
        commentCount: 6,
        imageUrls: [
            "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1518998053901-5348d3961a04?auto=format&fit=crop&w=1200&q=80"
        ],
        comments: [
            { id: 1, authorNickname: "平台管理员", content: "A useful angle here would be to add costume symbolism and hand gesture notes.", createdAt: "2026-04-05T09:30:00" },
            { id: 2, authorNickname: "小组成员1", content: "The scene description already feels like a museum interpretation panel.", createdAt: "2026-04-05T14:10:00" }
        ],
        createdAt: "2026-04-05T08:00:00"
    },
    102: {
        id: 102,
        title: "Reading the Timber Logic of Historic Covered Bridges",
        content: "A category detail page should be able to surface architecture essays like this one, where structural systems, repair history, and local materials are described visually and accessibly.",
        coverImageUrl: "https://images.unsplash.com/photo-1518005020951-eccb494ad742?auto=format&fit=crop&w=1200&q=80",
        heritageName: "廊桥营造技艺",
        region: "浙江温州",
        status: "PUBLISHED",
        authorName: "平台管理员",
        categoryName: "古建筑",
        likeCount: 27,
        favoriteCount: 12,
        commentCount: 4,
        imageUrls: [
            "https://images.unsplash.com/photo-1518005020951-eccb494ad742?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1529421306624-54a49f2d50f8?auto=format&fit=crop&w=1200&q=80"
        ],
        comments: [
            { id: 3, authorNickname: "小组成员1", content: "The bridge section drawings would fit nicely into the gallery area.", createdAt: "2026-04-06T10:05:00" }
        ],
        createdAt: "2026-04-04T11:20:00"
    },
    103: {
        id: 103,
        title: "Field Notes from an Indigo Dye Workshop",
        content: "This sample entry demonstrates how process photography, tool documentation, and oral explanation can live together in one article page without needing video playback.",
        coverImageUrl: "https://images.unsplash.com/photo-1517697471339-4aa32003c11a?auto=format&fit=crop&w=1200&q=80",
        heritageName: "蓝染技艺",
        region: "贵州黔东南",
        status: "PUBLISHED",
        authorName: "小组成员1",
        categoryName: "传统技艺",
        likeCount: 41,
        favoriteCount: 22,
        commentCount: 9,
        imageUrls: [
            "https://images.unsplash.com/photo-1517697471339-4aa32003c11a?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=80"
        ],
        comments: [
            { id: 4, authorNickname: "平台管理员", content: "This one is strong enough to appear in the homepage ranking module.", createdAt: "2026-04-07T15:20:00" }
        ],
        createdAt: "2026-04-03T16:40:00"
    },
    104: {
        id: 104,
        title: "Lantern Processions and Night Ritual in the Old City",
        content: "Festival-oriented content can sit in the homepage alongside craft and architecture posts, giving the archive a more social and seasonal rhythm.",
        coverImageUrl: "https://images.unsplash.com/photo-1513151233558-d860c5398176?auto=format&fit=crop&w=1200&q=80",
        heritageName: "元宵灯会",
        region: "福建泉州",
        status: "PUBLISHED",
        authorName: "小组成员1",
        categoryName: "民俗节庆",
        likeCount: 18,
        favoriteCount: 9,
        commentCount: 3,
        imageUrls: [
            "https://images.unsplash.com/photo-1513151233558-d860c5398176?auto=format&fit=crop&w=1200&q=80"
        ],
        comments: [],
        createdAt: "2026-04-02T18:15:00"
    },
    105: {
        id: 105,
        title: "Clay Figurine Colour Layers in Studio Practice",
        content: "This placeholder article supports the personal profile and category views by giving the platform a fuller archive for presentation and navigation testing.",
        coverImageUrl: "https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?auto=format&fit=crop&w=1200&q=80",
        heritageName: "泥塑",
        region: "天津",
        status: "PUBLISHED",
        authorName: "平台管理员",
        categoryName: "传统技艺",
        likeCount: 16,
        favoriteCount: 8,
        commentCount: 2,
        imageUrls: [
            "https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?auto=format&fit=crop&w=1200&q=80"
        ],
        comments: [],
        createdAt: "2026-04-01T13:00:00"
    },
    106: {
        id: 106,
        title: "Pattern Fragments from Shadow Puppet Costuming",
        content: "A richer front page works better when different article types can coexist: research notes, object studies, visual records, and curatorial reflections.",
        coverImageUrl: "https://images.unsplash.com/photo-1500534623283-312aade485b7?auto=format&fit=crop&w=1200&q=80",
        heritageName: "皮影戏",
        region: "陕西西安",
        status: "PUBLISHED",
        authorName: "平台管理员",
        categoryName: "传统戏曲",
        likeCount: 22,
        favoriteCount: 11,
        commentCount: 5,
        imageUrls: [
            "https://images.unsplash.com/photo-1500534623283-312aade485b7?auto=format&fit=crop&w=1200&q=80"
        ],
        comments: [],
        createdAt: "2026-03-30T09:00:00"
    }
};

const workspaceDraftTemplates = [
    {
        id: "draft-1",
        title: "Draft: Mapping Ritual Sound in Temple Courtyards",
        categoryName: "民俗节庆",
        updatedAt: "2026-04-10T19:30:00",
        note: "Waiting for audio transcript cleanup and one cover image."
    },
    {
        id: "draft-2",
        title: "Draft: Motif Variations in Rural Woodcarving",
        categoryName: "传统技艺",
        updatedAt: "2026-04-09T16:10:00",
        note: "Needs a stronger opening paragraph and workshop credits."
    }
];

const workspacePendingTemplates = [
    {
        id: "pending-1",
        title: "Pending Review: Courtyard Festival Route Documentation",
        categoryName: "民俗节庆",
        updatedAt: "2026-04-08T12:40:00",
        note: "Submitted to the editorial queue. Waiting for category approval."
    },
    {
        id: "pending-2",
        title: "Pending Review: Roof Ornament Sketches from the South Gate",
        categoryName: "古建筑",
        updatedAt: "2026-04-07T10:25:00",
        note: "Awaiting image quality check and curator comments."
    }
];

const permissionRequestTemplates = [
    {
        id: "request-1",
        title: "Curator Review Access",
        status: "APPROVED",
        createdAt: "2026-04-03T09:00:00",
        note: "Approved for collection curation preview and metadata editing."
    },
    {
        id: "request-2",
        title: "Extended Publishing Rights",
        status: "PENDING",
        createdAt: "2026-04-10T14:45:00",
        note: "Pending faculty review."
    }
];

const adminApprovalTemplates = [
    {
        id: "approval-1",
        title: "Permission Request: Regional Editor",
        applicant: "Archive Visitor",
        type: "Role application",
        status: "Pending",
        submittedAt: "2026-04-11T09:25:00"
    },
    {
        id: "approval-2",
        title: "Article Review: Temple Banner Iconography",
        applicant: "Team Member 1",
        type: "Article review",
        status: "Pending",
        submittedAt: "2026-04-11T15:40:00"
    }
];

const clone = (value) => JSON.parse(JSON.stringify(value));

const createEmptyPostForm = () => ({
    title: "",
    content: "",
    categoryId: "",
    coverImageUrl: "",
    heritageName: "",
    region: "",
    imageUrls: []
});

const draftCacheKey = "heritage-draft-cache";

const readStoredDraftCache = () => {
    const raw = localStorage.getItem(draftCacheKey);
    if (!raw) {
        return {};
    }

    try {
        return JSON.parse(raw);
    } catch (error) {
        localStorage.removeItem(draftCacheKey);
        return {};
    }
};

const readStoredUser = () => {
    const raw = localStorage.getItem("heritage-current-user");
    if (!raw) {
        return null;
    }

    try {
        return JSON.parse(raw);
    } catch (error) {
        localStorage.removeItem("heritage-current-user");
        return null;
    }
};

const normaliseSummary = (post, index = 0) => ({
    id: post.id ?? 1000 + index,
    title: post.title || "Untitled heritage article",
    coverImageUrl: post.coverImageUrl || "",
    heritageName: post.heritageName || "",
    region: post.region || "",
    status: post.status || "PUBLISHED",
    authorName: post.authorName || post.authorNickname || "Anonymous Contributor",
    categoryName: post.categoryName || "传统技艺",
    likeCount: Number(post.likeCount || 0),
    favoriteCount: Number(post.favoriteCount || 0),
    commentCount: Number(post.commentCount || 0),
    createdAt: post.createdAt || "2026-04-01T10:00:00"
});

const normaliseDetail = (detail, index = 0) => ({
    ...normaliseSummary(detail, index),
    content: detail.content || "This front-end preview card does not have a full article body yet.",
    imageUrls: Array.isArray(detail.imageUrls) ? detail.imageUrls : [],
    comments: Array.isArray(detail.comments) ? detail.comments : []
});

const buildFallbackSummaries = () =>
    Object.values(fallbackPostDetails).map((detail, index) => normaliseSummary(detail, index));

const mergeCategoriesWithFallback = (items = []) => {
    const seen = new Set();
    const merged = [];

    [...items, ...fallbackCategories].forEach((category, index) => {
        const name = category?.name;
        if (!name || seen.has(name)) {
            return;
        }

        seen.add(name);
        merged.push({
            id: category.id ?? 500 + index,
            name,
            description: category.description || categoryDescriptionMap[name] || "",
            createdAt: category.createdAt || "2026-04-01T10:00:00"
        });
    });

    return merged;
};

const mergePostsWithFallback = (items = []) => {
    const primary = items.map((post, index) => normaliseSummary(post, index));
    const merged = [...primary];
    const seenTitles = new Set(primary.map((post) => `${post.title}|${post.categoryName}`));

    for (const post of buildFallbackSummaries()) {
        const key = `${post.title}|${post.categoryName}`;
        if (seenTitles.has(key)) {
            continue;
        }
        merged.push(post);
        seenTitles.add(key);
        if (merged.length >= 8) {
            break;
        }
    }

    return merged;
};

createApp({
    data() {
        return {
            currentView: "home",
            loading: false,
            errorMessage: "",
            successMessage: "",
            categories: [],
            posts: [],
            myPosts: [],
            adminPosts: [],
            pendingQueuePosts: [],
            selectedPostId: null,
            selectedPost: null,
            selectedCategoryFilter: "",
            selectedCategoryFocus: "",
            homeSearchQuery: "",
            profileSection: "published",
            adminSection: "articles",
            currentUser: readStoredUser(),
            registerForm: {
                username: "",
                nickname: "",
                password: ""
            },
            loginForm: {
                username: "",
                password: ""
            },
            postForm: createEmptyPostForm(),
            editingPostId: null,
            editingPostStatus: "",
            editingRejectReason: "",
            coverFileLabel: "No file selected",
            galleryFileLabel: "No files selected",
            commentForm: {
                content: ""
            },
            workspaceDrafts: [],
            workspacePending: [],
            contributorApplications: [],
            adminUsers: [],
            adminContributorApplications: [],
            adminApprovals: [],
            adminFilterStatus: "",
            adminTitleQuery: "",
            adminSortOption: "UPDATED_DESC",
            pendingQueueQuery: "",
            pendingQueueReturnToApprovals: false,
            adminPostPage: 0,
            adminPostSize: 10,
            adminPostTotalElements: 0,
            adminPostTotalPages: 0,
            adminPostHasPrevious: false,
            adminPostHasNext: false,
            adminUserQuery: "",
            adminUserRoleFilter: "",
            adminUserActiveFilter: "",
            adminContributorApplicationQuery: "",
            adminContributorApplicationStatusFilter: "",
            adminContributorApplicationSortBy: "created_at_desc",
            contributorApplicationRejectReasons: {},
            selectedAdminPostId: null,
            selectedAdminPost: null,
            adminReviewReason: "",
            draftCache: readStoredDraftCache(),
            contributorApplicationForm: {
                applicationReason: "",
                attachment: null
            },
            attachmentFileLabel: "No file selected"
        };
    },
    computed: {
        isAdmin() {
            return this.currentUser?.role === "ADMIN";
        },
        isContributor() {
            return this.currentUser?.role === "CONTRIBUTOR" || this.currentUser?.role === "ADMIN";
        },
        currentViewLabel() {
            const labels = {
                home: "Homepage dashboard",
                detail: "Reading an article",
                publish: this.editingPostId ? "Editing a draft" : "Creating a draft",
                profile: "Personal workspace",
                admin: "Administration workspace",
                auth: "Managing account access"
            };
            return labels[this.currentView] || "Homepage dashboard";
        },
        totalStories() {
            return this.posts.length;
        },
        totalComments() {
            return this.posts.reduce((sum, post) => sum + Number(post.commentCount || 0), 0);
        },
        totalContributors() {
            return new Set(this.posts.map((post) => post.authorName).filter(Boolean)).size;
        },
        filteredPosts() {
            const query = this.homeSearchQuery.trim().toLowerCase();
            return this.posts.filter((post) => {
                const matchesCategory = !this.selectedCategoryFilter || post.categoryName === this.selectedCategoryFilter;
                const matchesSearch = !query || this.matchesSearch(post, query);
                return matchesCategory && matchesSearch;
            });
        },
        featuredPost() {
            return this.filteredPosts[0] || this.hotPosts[0] || this.posts[0] || null;
        },
        hotPosts() {
            return [...this.posts]
                .sort((left, right) => this.buildHeatScore(right) - this.buildHeatScore(left))
                .slice(0, 5);
        },
        categoryStats() {
            const categories = this.categories.length ? this.categories : mergeCategoriesWithFallback([]);
            const counts = categories.map((category) => {
                const posts = this.posts.filter((post) => post.categoryName === category.name);
                return {
                    name: category.name,
                    description: category.description || categoryDescriptionMap[category.name] || "",
                    count: posts.length,
                    topPost: [...posts].sort((left, right) => this.buildHeatScore(right) - this.buildHeatScore(left))[0] || null
                };
            });

            const maxCount = counts.reduce((max, item) => Math.max(max, item.count), 0);

            return counts
                .map((item) => ({
                    ...item,
                    percentage: maxCount === 0 ? 0 : Math.max(14, Math.round((item.count / maxCount) * 100))
                }))
                .sort((left, right) => right.count - left.count);
        },
        focusedCategory() {
            return this.selectedCategoryFocus || this.categoryStats[0]?.name || "";
        },
        focusedCategoryPosts() {
            if (!this.focusedCategory) {
                return [];
            }
            return this.posts.filter((post) => post.categoryName === this.focusedCategory);
        },
        profilePublishedPosts() {
            return this.myPosts.filter((post) => post.status === "PUBLISHED");
        },
        profileSummary() {
            return {
                published: this.profilePublishedPosts.length,
                pending: this.workspacePending.length,
                drafts: this.workspaceDrafts.length,
                requests: this.contributorApplications.length
            };
        },
        adminArticles() {
            return this.adminPosts;
        },
        adminResultSummary() {
            const filters = [];
            if (this.adminFilterStatus) {
                filters.push(this.statusLabel(this.adminFilterStatus));
            }
            if (this.adminTitleQuery.trim()) {
                filters.push(`"${this.adminTitleQuery.trim()}"`);
            }
            return filters.length ? `Showing: ${filters.join(" · ")}` : "Showing: All articles";
        },
        adminResultCountLabel() {
            const currentCount = this.adminArticles.length;
            const totalCount = this.adminPostTotalElements || 0;
            if (!totalCount) {
                return `${currentCount} result${currentCount === 1 ? "" : "s"}`;
            }
            return `${currentCount} of ${totalCount} result${totalCount === 1 ? "" : "s"}`;
        },
        adminSortSummary() {
            return `Sorted by: ${this.adminSortLabel(this.adminSortOption)}`;
        },
        adminPaginationLabel() {
            if (!this.adminPostTotalPages) {
                return "Page 1 of 1";
            }
            return `Page ${this.adminPostPage + 1} of ${this.adminPostTotalPages}`;
        },
        adminSortColumnLabel() {
            if (this.adminSortOption === "SUBMITTED_DESC") {
                return "Submitted";
            }
            if (this.adminSortOption === "REVIEWED_DESC") {
                return "Reviewed";
            }
            return "Updated";
        },
        adminUsersView() {
            return this.adminUsers
                .filter((user) => {
                    if (this.adminUserActiveFilter === "active") {
                        return user.active === true;
                    }
                    if (this.adminUserActiveFilter === "inactive") {
                        return user.active === false;
                    }
                    return true;
                })
                .map((user) => ({
                    ...user,
                    status: user.active ? "Active" : "Inactive",
                    contributions: user.contributionCount || 0
                }));
        },
        adminUserResultCountLabel() {
            const count = this.adminUsersView.length;
            return `${count} result${count === 1 ? "" : "s"}`;
        },
        adminUserFilterSummary() {
            const parts = [];

            if (this.adminUserRoleFilter) {
                parts.push(this.roleLabel(this.adminUserRoleFilter));
            }

            if (this.adminUserActiveFilter === "active") {
                parts.push("Active");
            } else if (this.adminUserActiveFilter === "inactive") {
                parts.push("Inactive");
            }

            if (this.adminUserQuery.trim()) {
                parts.push(`"${this.adminUserQuery.trim()}"`);
            }

            if (!parts.length) {
                return "Showing: All users";
            }

            return `Showing: ${parts.join(" · ")}`;
        },
        adminContributorApplicationsView() {
            const query = this.adminContributorApplicationQuery.trim().toLowerCase();
            return this.adminContributorApplications.filter((application) => {
                if (!query) {
                    return true;
                }
                return String(application.applicantUsername || "")
                    .toLowerCase()
                    .includes(query);
            });
        },
        adminContributorApplicationResultCountLabel() {
            const count = this.adminContributorApplicationsView.length;
            return `${count} result${count === 1 ? "" : "s"}`;
        },
        adminContributorApplicationFilterSummary() {
            const parts = [];

            if (this.adminContributorApplicationStatusFilter) {
                parts.push(this.statusLabel(this.adminContributorApplicationStatusFilter));
            }

            if (this.adminContributorApplicationQuery.trim()) {
                parts.push(`"${this.adminContributorApplicationQuery.trim()}"`);
            }

            if (!parts.length) {
                return "Showing: All applications";
            }

            return `Showing: ${parts.join(" · ")}`;
        },
        adminContributorApplicationEmptyLabel() {
            if (!this.adminContributorApplications.length) {
                return "No contributor applications submitted yet.";
            }
            return "No contributor applications match the current filter.";
        },
        adminApprovalsView() {
            const query = this.pendingQueueQuery.trim().toLowerCase();
            return this.pendingQueuePosts
                .filter((post) => !query || [post.title, this.translateText(post.title)]
                    .filter(Boolean)
                    .join(" ")
                    .toLowerCase()
                    .includes(query))
                .map((post) => ({
                    id: `approval-${post.id}`,
                    postId: post.id,
                    title: post.title,
                    authorName: post.authorName,
                    reviewerName: post.reviewerName || "",
                    status: post.status,
                    submittedAt: post.submittedAt || post.updatedAt
                }));
        },
        pendingQueueSummaryLabel() {
            const waitingCount = this.pendingQueuePosts.length;
            const filteredCount = this.adminApprovalsView.length;
            if (!waitingCount) {
                return "0 items waiting";
            }
            if (!this.pendingQueueQuery.trim()) {
                return `${waitingCount} item${waitingCount === 1 ? "" : "s"} waiting`;
            }
            return `${filteredCount} of ${waitingCount} item${waitingCount === 1 ? "" : "s"} waiting`;
        },
        pendingQueueSearchSummary() {
            if (!this.pendingQueueQuery.trim()) {
                return "Queue shows all pending items only.";
            }
            return `Searching queue titles for "${this.pendingQueueQuery.trim()}".`;
        },
        pendingQueueEmptyLabel() {
            if (!this.pendingQueuePosts.length) {
                return "No pending article reviews right now.";
            }
            return "No pending article reviews match the current queue search.";
        },
        canApplyForContributor() {
            return this.currentUser?.role === "USER"
                && !this.contributorApplications.some((application) => application.status === "PENDING");
        },
        latestContributorApplication() {
            return this.contributorApplications[0] || null;
        },
        canSubmitForReview() {
            return Boolean(this.editingPostId) && ["DRAFT", "REJECTED"].includes(this.editingPostStatus);
        },
        publishPrimaryLabel() {
            return this.editingPostId ? "Update draft" : "Save draft";
        }
    },
    async mounted() {
        await Promise.all([this.fetchCategories(), this.fetchPosts()]);
        if (this.currentUser) {
            await this.refreshWorkspaceData();
        }
        this.handleHash();
        window.addEventListener("hashchange", this.handleHash);
    },
    beforeUnmount() {
        window.removeEventListener("hashchange", this.handleHash);
    },
    methods: {
        navigate(view, updateHash = true) {
            let nextView = view;

            if (view === "profile" && !this.currentUser) {
                this.showError("Please sign in to open your personal workspace.");
                nextView = "auth";
            }

            if (view === "publish" && !this.isContributor) {
                this.showError("Please sign in as a contributor to access the drafts page.");
                nextView = this.currentUser ? "profile" : "auth";
            }

            if (view === "admin" && !this.isAdmin) {
                this.showError("Sign in with the administrator account to open the admin preview.");
                nextView = this.currentUser ? "profile" : "auth";
            }

            this.currentView = nextView;
            if (nextView === "profile" && this.currentUser) {
                this.refreshWorkspaceData();
            }
            if (nextView === "admin" && this.isAdmin) {
                this.fetchAdminUsers();
                if (this.adminSection === "users") {
                    this.fetchAdminContributorApplications();
                }
                if (this.adminSection === "approvals") {
                    this.fetchPendingQueue();
                } else {
                    this.fetchAdminPosts();
                }
            }
            if (updateHash) {
                window.location.hash = `#${nextView}`;
            }
        },
        handleHash() {
            const hash = window.location.hash.replace("#", "");
            if (!hash || hash === "home") {
                this.currentView = "home";
                return;
            }

            if (hash.startsWith("detail-")) {
                const id = Number(hash.replace("detail-", ""));
                if (id) {
                    this.openPost(id);
                }
                return;
            }

            if (hash === "categories") {
                this.navigate("home", false);
                this.focusCollections(false);
                return;
            }

            const supported = ["home", "publish", "profile", "admin", "auth"];
            if (supported.includes(hash)) {
                this.navigate(hash, false);
            }
        },
        authHeaders(baseHeaders = {}) {
            const headers = { ...baseHeaders };
            if (this.currentUser?.token) {
                headers["Authorization"] = `Bearer ${this.currentUser.token}`;
            }
            return headers;
        },
        async fetchCategories() {
            try {
                const response = await fetch("/api/categories");
                const payload = await response.json();
                this.categories = mergeCategoriesWithFallback(payload.data || []);
            } catch (error) {
                this.categories = mergeCategoriesWithFallback([]);
            } finally {
                if (!this.selectedCategoryFocus && this.categories.length) {
                    this.selectedCategoryFocus = this.categories[0].name;
                }
            }
        },
        async fetchPosts() {
            this.loading = true;
            try {
                const response = await fetch("/api/posts");
                const payload = await response.json();
                this.posts = mergePostsWithFallback(payload.data || []);
            } catch (error) {
                this.posts = mergePostsWithFallback([]);
                this.showError("The live feed is unavailable, so the front-end demo content is being shown instead.");
            } finally {
                this.loading = false;
            }
        },
        async fetchMyPosts() {
            if (!this.currentUser) {
                this.myPosts = [];
                this.workspaceDrafts = [];
                this.workspacePending = [];
                return;
            }
            const data = await this.request("/api/my/posts", {
                method: "GET"
            }, "", null, true);
            if (!Array.isArray(data)) {
                return;
            }
            this.myPosts = data;
            this.workspaceDrafts = data.filter((post) => post.status === "DRAFT" || post.status === "REJECTED");
            this.workspacePending = data.filter((post) => post.status === "PENDING_REVIEW");
            if (this.editingPostId) {
                const currentEditingPost = data.find((post) => post.id === this.editingPostId);
                if (currentEditingPost) {
                    this.editingPostStatus = currentEditingPost.status;
                    this.editingRejectReason = currentEditingPost.rejectReason || "";
                }
            }
        },
        async fetchMyContributorApplications() {
            if (!this.currentUser) {
                this.contributorApplications = [];
                return;
            }
            const data = await this.request("/api/my/contributor-applications", {
                method: "GET"
            }, "", null, true);
            if (Array.isArray(data)) {
                this.contributorApplications = data;
            }
        },
        async fetchAdminPosts() {
            if (!this.isAdmin) {
                this.adminPosts = [];
                this.adminPostTotalElements = 0;
                this.adminPostTotalPages = 0;
                this.adminPostHasPrevious = false;
                this.adminPostHasNext = false;
                return;
            }
            const params = new URLSearchParams();
            if (this.adminFilterStatus) {
                params.set("status", this.adminFilterStatus);
            }
            if (this.adminTitleQuery.trim()) {
                params.set("title", this.adminTitleQuery.trim());
            }
            if (this.adminSortOption) {
                params.set("sort", this.adminSortOption);
            }
            params.set("page", String(this.adminPostPage));
            params.set("size", String(this.adminPostSize));
            const query = params.toString();
            this.errorMessage = "";
            try {
                const response = await fetch(`/api/admin/posts${query ? `?${query}` : ""}`, {
                    method: "GET",
                    headers: this.authHeaders()
                });
                const payload = await response.json();
                if (!payload.success) {
                    throw new Error(this.translateBackendMessage(payload.message) || "The request could not be completed.");
                }
                const data = Array.isArray(payload.data) ? payload.data : [];
                const totalElements = Number(response.headers.get("X-Total-Elements") || data.length || 0);
                const totalPages = Number(response.headers.get("X-Total-Pages") || (data.length ? 1 : 0));
                const hasPrevious = (response.headers.get("X-Has-Previous") || "false") === "true";
                const hasNext = (response.headers.get("X-Has-Next") || "false") === "true";

                if (this.adminPostPage > 0 && !data.length && totalElements > 0) {
                    this.adminPostPage -= 1;
                    await this.fetchAdminPosts();
                    return;
                }

                this.adminPosts = data;
                this.adminPostTotalElements = totalElements;
                this.adminPostTotalPages = totalPages;
                this.adminPostHasPrevious = hasPrevious;
                this.adminPostHasNext = hasNext;
            } catch (error) {
                this.showError(error.message || "The request could not be completed.");
            }
        },
        async fetchPendingQueue() {
            if (!this.isAdmin) {
                this.pendingQueuePosts = [];
                return;
            }
            const data = await this.request("/api/admin/posts?status=PENDING_REVIEW", {
                method: "GET"
            }, "", null, true);
            if (Array.isArray(data)) {
                this.pendingQueuePosts = data;
            }
        },
        async fetchAdminContributorApplications() {
            if (!this.isAdmin) {
                this.adminContributorApplications = [];
                return;
            }
            const params = new URLSearchParams();
            if (this.adminContributorApplicationStatusFilter) {
                params.set("status", this.adminContributorApplicationStatusFilter);
            }
            if (this.adminContributorApplicationSortBy) {
                params.set("sortBy", this.adminContributorApplicationSortBy);
            }
            const query = params.toString();
            const data = await this.request(`/api/admin/contributor-applications${query ? `?${query}` : ""}`, {
                method: "GET"
            }, "", null, true);
            if (Array.isArray(data)) {
                this.adminContributorApplications = data;
                const nextRejectReasons = {};
                for (const application of data) {
                    nextRejectReasons[application.id] = this.contributorApplicationRejectReasons[application.id] || "";
                }
                this.contributorApplicationRejectReasons = nextRejectReasons;
            }
        },
        async fetchAdminUsers() {
            if (!this.isAdmin) {
                this.adminUsers = [];
                return;
            }
            const params = new URLSearchParams();
            if (this.adminUserQuery.trim()) {
                params.set("username", this.adminUserQuery.trim());
            }
            if (this.adminUserRoleFilter) {
                params.set("role", this.adminUserRoleFilter);
            }
            const query = params.toString();
            const data = await this.request(`/api/admin/users${query ? `?${query}` : ""}`, {
                method: "GET"
            }, "", null, true);
            if (Array.isArray(data)) {
                this.adminUsers = data;
            }
        },
        async searchAdminPosts() {
            this.adminPostPage = 0;
            await this.fetchAdminPosts();
        },
        async clearAdminSearch() {
            this.adminTitleQuery = "";
            this.adminFilterStatus = "";
            this.adminSortOption = "UPDATED_DESC";
            this.adminPostPage = 0;
            this.adminPostSize = 10;
            await this.fetchAdminPosts();
        },
        async goToPreviousAdminPostPage() {
            if (!this.adminPostHasPrevious || this.adminPostPage <= 0) {
                return;
            }
            this.adminPostPage -= 1;
            await this.fetchAdminPosts();
        },
        async goToNextAdminPostPage() {
            if (!this.adminPostHasNext) {
                return;
            }
            this.adminPostPage += 1;
            await this.fetchAdminPosts();
        },
        async changeAdminPostSize() {
            this.adminPostPage = 0;
            await this.fetchAdminPosts();
        },
        async searchAdminUsers() {
            await this.fetchAdminUsers();
        },
        async clearAdminUserSearch() {
            this.adminUserQuery = "";
            this.adminUserRoleFilter = "";
            this.adminUserActiveFilter = "";
            await this.fetchAdminUsers();
        },
        async searchAdminContributorApplications() {
            await this.fetchAdminContributorApplications();
        },
        async clearAdminContributorApplications() {
            this.adminContributorApplicationQuery = "";
            this.adminContributorApplicationStatusFilter = "";
            await this.fetchAdminContributorApplications();
        },
        async refreshWorkspaceData() {
            await this.fetchMyPosts();
            await this.fetchMyContributorApplications();
            if (this.isAdmin) {
                await this.fetchAdminPosts();
                await this.fetchAdminUsers();
                await this.fetchPendingQueue();
                await this.fetchAdminContributorApplications();
            }
        },
        async refreshAdminViews() {
            if (!this.isAdmin) {
                this.adminPosts = [];
                this.pendingQueuePosts = [];
                return;
            }
            await this.fetchPendingQueue();
            if (this.adminSection === "articles" || this.selectedAdminPostId) {
                await this.fetchAdminPosts();
            }
        },
        async openPost(postId) {
            if (fallbackPostDetails[postId]) {
                this.selectedPost = normaliseDetail(clone(fallbackPostDetails[postId]));
                this.selectedPostId = postId;
                this.currentView = "detail";
                if (window.location.hash !== `#detail-${postId}`) {
                    window.location.hash = `#detail-${postId}`;
                }
                return;
            }

            this.loading = true;
            this.errorMessage = "";
            try {
                const response = await fetch(`/api/posts/${postId}`);
                const payload = await response.json();
                if (!payload.success) {
                    throw new Error(this.translateBackendMessage(payload.message) || "Unable to load this article.");
                }
                this.selectedPost = normaliseDetail(payload.data);
                this.selectedPostId = postId;
                this.currentView = "detail";
                if (window.location.hash !== `#detail-${postId}`) {
                    window.location.hash = `#detail-${postId}`;
                }
            } catch (error) {
                const summary = this.posts.find((post) => post.id === postId);
                if (summary) {
                    this.selectedPost = normaliseDetail({
                        ...summary,
                        content: "This article is available in the front-end preview, but the live detail endpoint is not currently returning a full payload.",
                        imageUrls: summary.coverImageUrl ? [summary.coverImageUrl] : [],
                        comments: []
                    });
                    this.selectedPostId = postId;
                    this.currentView = "detail";
                    if (window.location.hash !== `#detail-${postId}`) {
                        window.location.hash = `#detail-${postId}`;
                    }
                } else {
                    this.showError(error.message || "Unable to load this article.");
                }
            } finally {
                this.loading = false;
            }
        },
        inspectCategory(categoryName) {
            this.selectedCategoryFocus = categoryName;
            this.selectedCategoryFilter = categoryName;
            this.navigate("home");
            this.scrollToCollections();
        },
        applyCategoryToSearch(categoryName) {
            this.selectedCategoryFilter = categoryName;
            this.navigate("home");
        },
        focusCollections(shouldNavigate = true) {
            if (shouldNavigate) {
                this.navigate("home");
            }
            this.scrollToCollections();
        },
        scrollToCollections() {
            this.$nextTick(() => {
                const section = this.$refs.homeCollectionsSection;
                if (section?.scrollIntoView) {
                    section.scrollIntoView({ behavior: "smooth", block: "start" });
                }
            });
        },
        clearHomeSearch() {
            this.homeSearchQuery = "";
            this.selectedCategoryFilter = "";
        },
        async selectProfileSection(section) {
            this.profileSection = section;
            if (this.currentUser) {
                await this.fetchMyPosts();
                await this.fetchMyContributorApplications();
            }
        },
        async selectAdminSection(section) {
            this.adminSection = section;
            if (section !== "articles") {
                this.pendingQueueReturnToApprovals = false;
            }
            if (this.isAdmin && section === "articles") {
                await this.fetchAdminPosts();
            }
            if (this.isAdmin && section === "users") {
                await this.fetchAdminUsers();
                await this.fetchAdminContributorApplications();
            }
            if (this.isAdmin && section === "approvals") {
                await this.fetchPendingQueue();
            }
        },
        resolveCategoryId(categoryName) {
            const match = this.categories.find((category) => category.name === categoryName);
            return match ? String(match.id) : "";
        },
        storeDraftSnapshot(post) {
            if (!post?.id) {
                return;
            }
            this.draftCache[String(post.id)] = post;
            localStorage.setItem(draftCacheKey, JSON.stringify(this.draftCache));
        },
        removeDraftSnapshot(postId) {
            if (!postId) {
                return;
            }
            delete this.draftCache[String(postId)];
            localStorage.setItem(draftCacheKey, JSON.stringify(this.draftCache));
        },
        resetPostEditor() {
            this.postForm = createEmptyPostForm();
            this.editingPostId = null;
            this.editingPostStatus = "";
            this.editingRejectReason = "";
            this.coverFileLabel = "No file selected";
            this.galleryFileLabel = "No files selected";
        },
        applyPostDetailToEditor(postDetail, summaryEntry = null) {
            const fallbackEntry = summaryEntry || {};
            const resolvedCategoryId = postDetail.categoryId
                ? String(postDetail.categoryId)
                : this.resolveCategoryId(postDetail.categoryName || fallbackEntry.categoryName || "");

            this.editingPostId = postDetail.id || fallbackEntry.id || null;
            this.editingPostStatus = postDetail.status || fallbackEntry.status || "";
            this.editingRejectReason = postDetail.rejectReason || fallbackEntry.rejectReason || "";
            this.postForm = {
                title: postDetail.title || fallbackEntry.title || "",
                content: postDetail.content || fallbackEntry.content || "",
                categoryId: resolvedCategoryId,
                coverImageUrl: postDetail.coverImageUrl || fallbackEntry.coverImageUrl || "",
                heritageName: postDetail.heritageName || fallbackEntry.heritageName || "",
                region: postDetail.region || fallbackEntry.region || "",
                imageUrls: Array.isArray(postDetail.imageUrls)
                    ? [...postDetail.imageUrls]
                    : Array.isArray(fallbackEntry.imageUrls)
                        ? [...fallbackEntry.imageUrls]
                        : []
            };
            this.coverFileLabel = this.postForm.coverImageUrl ? this.postForm.coverImageUrl.split("/").pop() : "No file selected";
            this.galleryFileLabel = this.postForm.imageUrls.length
                ? `${this.postForm.imageUrls.length} files selected`
                : "No files selected";
            this.navigate("publish");
        },
        async editPost(entry) {
            const cached = this.draftCache[String(entry.id)] || null;
            const data = await this.request(`/api/my/posts/${entry.id}`, {
                method: "GET"
            }, "", null, true);

            if (data) {
                this.applyPostDetailToEditor(data, entry);
                this.storeDraftSnapshot({
                    ...data,
                    categoryId: data.categoryId || this.resolveCategoryId(data.categoryName || entry.categoryName || ""),
                    rejectReason: entry.rejectReason || ""
                });
                return;
            }

            if (cached) {
                this.showSuccess("Using the latest local draft snapshot because the live draft detail could not be loaded.");
                this.applyPostDetailToEditor(cached, entry);
                return;
            }

            this.showError("This article could not be re-opened because the live draft detail is unavailable and no local draft snapshot was found.");
        },
        async openAdminPost(postId) {
            this.selectedAdminPostId = postId;
            const data = await this.request(`/api/admin/posts/${postId}`, {
                method: "GET"
            }, "", null, true);
            if (data) {
                this.selectedAdminPost = data;
                this.adminReviewReason = data.rejectReason || "";
            }
        },
        async submitPermissionRequest() {
            console.log("=== submitPermissionRequest called ===");
            console.log("currentUser:", this.currentUser);
            console.log("applicationReason:", this.contributorApplicationForm.applicationReason);
            console.log("attachment:", this.contributorApplicationForm.attachment);
            
            if (!this.currentUser) {
                console.error("User not logged in!");
                this.showError("请先登录");
                return;
            }
            
            if (!this.contributorApplicationForm.applicationReason.trim()) {
                this.showError("申请理由不能为空");
                return;
            }

            const formData = new FormData();
            formData.append("request", JSON.stringify({
                applicationReason: this.contributorApplicationForm.applicationReason
            }));
            if (this.contributorApplicationForm.attachment) {
                formData.append("attachment", this.contributorApplicationForm.attachment);
            }

            console.log("Sending request to /api/my/contributor-applications");
            const data = await this.request("/api/my/contributor-applications", {
                method: "POST",
                body: formData
            }, "Contributor application submitted successfully.", null, true);
            console.log("Response data:", data);
            if (data) {
                await this.fetchMyContributorApplications();
                this.profileSection = "permissions";
                this.contributorApplicationForm = {
                    applicationReason: "",
                    attachment: null
                };
                this.attachmentFileLabel = "No file selected";
            }
        },
        handleAttachmentChange(event) {
            const file = event.target.files[0];
            if (file) {
                if (file.type !== "application/pdf") {
                    this.showError("只支持PDF格式的附件");
                    event.target.value = "";
                    return;
                }
                this.contributorApplicationForm.attachment = file;
                this.attachmentFileLabel = file.name;
            } else {
                this.contributorApplicationForm.attachment = null;
                this.attachmentFileLabel = "No file selected";
            }
        },
        async register() {
            await this.request("/api/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(this.registerForm)
            }, "Registration complete. You are now signed in.", async (data) => {
                this.currentUser = data;
                this.storeUser(data);
                this.registerForm = { username: "", nickname: "", password: "" };
                await this.refreshWorkspaceData();
                this.navigate("profile");
            });
        },
        async login() {
            await this.request("/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(this.loginForm)
            }, "Signed in successfully.", async (data) => {
                this.currentUser = data;
                this.storeUser(data);
                this.loginForm = { username: "", password: "" };
                await this.refreshWorkspaceData();
                this.navigate("home");
            });
        },
        logout() {
            this.currentUser = null;
            this.selectedPostId = null;
            this.selectedPost = null;
            this.selectedAdminPostId = null;
            this.selectedAdminPost = null;
            this.profileSection = "published";
            this.adminSection = "articles";
            this.pendingQueueQuery = "";
            this.pendingQueueReturnToApprovals = false;
            this.myPosts = [];
            this.contributorApplications = [];
            this.adminPosts = [];
            this.adminUsers = [];
            this.pendingQueuePosts = [];
            this.adminContributorApplications = [];
            this.workspaceDrafts = [];
            this.workspacePending = [];
            this.resetPostEditor();
            localStorage.removeItem("heritage-current-user");
            this.navigate("home");
            this.showSuccess("You have been signed out.");
        },
        async submitPost() {
            if (!this.currentUser) {
                this.showError("Please sign in before saving a draft.");
                this.navigate("auth");
                return;
            }

            const payload = {
                ...this.postForm,
                categoryId: Number(this.postForm.categoryId)
            };

            const url = this.editingPostId ? `/api/posts/${this.editingPostId}` : "/api/posts";
            const method = this.editingPostId ? "PUT" : "POST";
            const data = await this.request(url, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            }, this.editingPostId ? "Draft updated successfully." : "Draft saved successfully.", null, true);

            if (data) {
                this.editingPostId = data.id;
                this.editingPostStatus = data.status;
                this.storeDraftSnapshot(data);
                await this.fetchMyPosts();
                this.profileSection = "drafts";
            }
        },
        async submitCurrentPostForReview() {
            if (!this.canSubmitForReview) {
                return;
            }
            const data = await this.request(`/api/posts/${this.editingPostId}/submit-review`, {
                method: "POST"
            }, "Article submitted for review.", null, true);

            if (data) {
                this.removeDraftSnapshot(this.editingPostId);
                this.resetPostEditor();
                await this.fetchMyPosts();
                this.profileSection = "pending";
                this.navigate("profile");
            }
        },
        triggerFilePicker(refName) {
            const input = this.$refs[refName];
            if (input) {
                input.click();
            }
        },
        async submitComment() {
            if (!this.currentUser) {
                this.showError("Please sign in before posting a comment.");
                this.navigate("auth");
                return;
            }
            if (!this.selectedPostId) {
                this.showError("Please open an article before posting a comment.");
                return;
            }

            const data = await this.request(`/api/posts/${this.selectedPostId}/comments`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    content: this.commentForm.content
                })
            }, "Comment posted successfully.", null, true);

            if (data) {
                this.commentForm.content = "";
                await this.fetchPosts();
                await this.openPost(this.selectedPostId);
            }
        },
        async handleCoverUpload(event) {
            const file = event.target.files[0];
            if (!file) {
                this.coverFileLabel = "No file selected";
                return;
            }
            this.coverFileLabel = file.name;
            const result = await this.uploadImage(file);
            if (result) {
                this.postForm.coverImageUrl = result.url;
            }
            event.target.value = "";
        },
        async handleGalleryUpload(event) {
            const files = Array.from(event.target.files || []);
            if (!files.length) {
                this.galleryFileLabel = "No files selected";
                return;
            }
            this.galleryFileLabel = files.length === 1 ? files[0].name : `${files.length} files selected`;
            for (const file of files) {
                const result = await this.uploadImage(file);
                if (result) {
                    this.postForm.imageUrls.push(result.url);
                }
            }
            event.target.value = "";
        },
        async uploadImage(file) {
            const formData = new FormData();
            formData.append("file", file);
            try {
                const response = await fetch("/api/uploads/images", {
                    method: "POST",
                    headers: this.authHeaders(),
                    body: formData
                });
                const payload = await response.json();
                if (!payload.success) {
                    throw new Error(this.translateBackendMessage(payload.message) || "Image upload failed.");
                }
                this.showSuccess(`Image uploaded: ${payload.data.fileName}`);
                return payload.data;
            } catch (error) {
                this.showError(error.message || "Image upload failed.");
                return null;
            }
        },
        async approveSelectedAdminPost() {
            if (!this.selectedAdminPostId) {
                return;
            }
            const reviewedPostId = this.selectedAdminPostId;
            const data = await this.request(`/api/admin/posts/${this.selectedAdminPostId}/review`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ action: "APPROVE", reason: "" })
            }, "Article approved successfully.", null, true);
            if (data) {
                this.adminReviewReason = "";
                await this.refreshAdminViews();
                if (this.pendingQueueReturnToApprovals && data.status !== "PENDING_REVIEW") {
                    this.selectedAdminPostId = reviewedPostId;
                    this.selectedAdminPost = data;
                    this.pendingQueueReturnToApprovals = false;
                    this.adminSection = "approvals";
                } else {
                    await this.openAdminPost(this.selectedAdminPostId);
                }
                await this.fetchPosts();
            }
        },
        async rejectSelectedAdminPost() {
            if (!this.selectedAdminPostId) {
                return;
            }
            const reviewedPostId = this.selectedAdminPostId;
            if (!this.adminReviewReason.trim()) {
                this.showError("Please provide a rejection reason before rejecting this article.");
                return;
            }
            const data = await this.request(`/api/admin/posts/${this.selectedAdminPostId}/review`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ action: "REJECT", reason: this.adminReviewReason })
            }, "Article rejected successfully.", null, true);
            if (data) {
                await this.refreshAdminViews();
                if (this.pendingQueueReturnToApprovals && data.status !== "PENDING_REVIEW") {
                    this.selectedAdminPostId = reviewedPostId;
                    this.selectedAdminPost = data;
                    this.pendingQueueReturnToApprovals = false;
                    this.adminSection = "approvals";
                } else {
                    await this.openAdminPost(this.selectedAdminPostId);
                }
            }
        },
        async quickApproveFromQueue(postId) {
            const data = await this.request(`/api/admin/posts/${postId}/review`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ action: "APPROVE", reason: "" })
            }, "Article approved successfully.", null, true);
            if (data) {
                if (this.selectedAdminPostId === postId) {
                    this.selectedAdminPost = data;
                }
                await this.refreshAdminViews();
                await this.fetchPosts();
            }
        },
        async openReviewFromQueue(postId) {
            this.pendingQueueReturnToApprovals = true;
            await this.selectAdminSection("articles");
            await this.openAdminPost(postId);
        },
        async returnToPendingQueue() {
            this.adminSection = "approvals";
            this.pendingQueueReturnToApprovals = false;
            await this.fetchPendingQueue();
        },
        clearPendingQueueSearch() {
            this.pendingQueueQuery = "";
        },
        async updateAdminUserRole(userId, role) {
            const data = await this.request(`/api/admin/users/${userId}/role`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ role })
            }, "User role updated successfully.", null, true);
            if (data) {
                await this.fetchAdminUsers();
            }
        },
        async activateAdminUser(userId) {
            const data = await this.request(`/api/admin/users/${userId}/activate`, {
                method: "POST"
            }, "User account activated successfully.", null, true);
            if (data) {
                await this.fetchAdminUsers();
            }
        },
        async deactivateAdminUser(userId) {
            const data = await this.request(`/api/admin/users/${userId}/deactivate`, {
                method: "POST"
            }, "User account deactivated successfully.", null, true);
            if (data) {
                await this.fetchAdminUsers();
            }
        },
        async approveContributorApplication(applicationId) {
            const data = await this.request(`/api/admin/contributor-applications/${applicationId}/approve`, {
                method: "POST"
            }, "Contributor application approved successfully.", null, true);
            if (data) {
                this.contributorApplicationRejectReasons[applicationId] = "";
                await this.fetchAdminContributorApplications();
                await this.fetchAdminUsers();
            }
        },
        async rejectContributorApplication(applicationId) {
            const reason = (this.contributorApplicationRejectReasons[applicationId] || "").trim();
            if (!reason) {
                this.showError("Please provide a rejection reason before rejecting this application.");
                return;
            }
            const data = await this.request(`/api/admin/contributor-applications/${applicationId}/reject`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ reason })
            }, "Contributor application rejected successfully.", null, true);
            if (data) {
                this.contributorApplicationRejectReasons[applicationId] = "";
                await this.fetchAdminContributorApplications();
                await this.fetchAdminUsers();
            }
        },
        async archiveSelectedAdminPost() {
            if (!this.selectedAdminPostId) {
                return;
            }
            const data = await this.request(`/api/admin/posts/${this.selectedAdminPostId}/archive`, {
                method: "POST"
            }, "Article archived successfully.", null, true);
            if (data) {
                await this.refreshAdminViews();
                await this.openAdminPost(this.selectedAdminPostId);
                await this.fetchPosts();
            }
        },
        async restoreSelectedAdminPost() {
            if (!this.selectedAdminPostId) {
                return;
            }
            const data = await this.request(`/api/admin/posts/${this.selectedAdminPostId}/restore`, {
                method: "POST"
            }, "Article restored successfully.", null, true);
            if (data) {
                await this.refreshAdminViews();
                await this.openAdminPost(this.selectedAdminPostId);
                await this.fetchPosts();
            }
        },
        async request(url, options = {}, successMessage = "", onSuccess, requiresAuth = false) {
            this.errorMessage = "";
            try {
                const headers = requiresAuth ? this.authHeaders(options.headers || {}) : (options.headers || {});
                const response = await fetch(url, {
                    ...options,
                    headers
                });
                const payload = await response.json();
                if (!payload.success) {
                    throw new Error(this.translateBackendMessage(payload.message) || "The request could not be completed.");
                }
                if (successMessage) {
                    this.showSuccess(successMessage);
                }
                if (onSuccess) {
                    await onSuccess(payload.data);
                }
                return payload.data;
            } catch (error) {
                this.showError(error.message || "The request could not be completed.");
                return null;
            }
        },
        matchesSearch(post, query) {
            const fields = [
                post.title,
                this.translateText(post.title),
                post.heritageName,
                this.translateHeritageName(post.heritageName),
                post.region,
                this.translateRegion(post.region),
                post.authorName,
                this.translateNickname(post.authorName),
                post.categoryName,
                this.translateCategory(post.categoryName)
            ];

            return fields
                .filter(Boolean)
                .join(" ")
                .toLowerCase()
                .includes(query);
        },
        buildHeatScore(post) {
            return Number(post.likeCount || 0) * 3 + Number(post.favoriteCount || 0) * 2 + Number(post.commentCount || 0) * 4;
        },
        statusLabel(status) {
            const mapping = {
                PUBLISHED: "Published",
                PENDING: "Pending",
                PENDING_REVIEW: "Pending Review",
                APPROVED: "Approved",
                DRAFT: "Draft",
                REJECTED: "Rejected",
                ARCHIVED: "Archived"
            };
            return mapping[status] || status || "Pending";
        },
        adminSortLabel(sort) {
            const mapping = {
                UPDATED_DESC: "Recently updated",
                SUBMITTED_DESC: "Recently submitted",
                REVIEWED_DESC: "Recently reviewed"
            };
            return mapping[sort] || "Recently updated";
        },
        roleLabel(role) {
            const mapping = {
                USER: "User",
                CONTRIBUTOR: "Contributor",
                ADMIN: "Admin"
            };
            return mapping[role] || role || "User";
        },
        statusClass(status) {
            const label = this.statusLabel(status).toLowerCase().replace(/\s+/g, "-");
            return `status-chip--${label}`;
        },
        adminSortValue(row) {
            if (this.adminSortOption === "SUBMITTED_DESC") {
                return row.submittedAt || row.updatedAt;
            }
            if (this.adminSortOption === "REVIEWED_DESC") {
                return row.reviewedAt || row.updatedAt;
            }
            return row.updatedAt;
        },
        reviewerLabel(post) {
            return post?.reviewerName || post?.reviewedBy || "";
        },
        canManageUserRole(user) {
            return user?.role === "USER" || user?.role === "CONTRIBUTOR";
        },
        nextUserRole(user) {
            return user?.role === "USER" ? "CONTRIBUTOR" : "USER";
        },
        nextUserRoleLabel(user) {
            return user?.role === "USER" ? "Promote to Contributor" : "Change to User";
        },
        nextUserActiveActionLabel(user) {
            return user?.active ? "Deactivate" : "Activate";
        },
        contributorApplicationTitle() {
            return "Contributor Access Application";
        },
        contributorApplicationNote(application) {
            if (application?.status === "PENDING") {
                return "Your contributor application is waiting for administrator review.";
            }
            if (application?.status === "APPROVED") {
                return "Your contributor application was approved.";
            }
            if (application?.status === "REJECTED") {
                return application?.rejectReason
                    ? `Your contributor application was rejected: ${application.rejectReason}`
                    : "Your contributor application was rejected.";
            }
            return "";
        },
        showError(message) {
            this.errorMessage = message;
            this.successMessage = "";
        },
        showSuccess(message) {
            this.successMessage = message;
            this.errorMessage = "";
            window.setTimeout(() => {
                if (this.successMessage === message) {
                    this.successMessage = "";
                }
            }, 2600);
        },
        formatDate(value) {
            if (!value) {
                return "";
            }
            return new Date(value).toLocaleString("en-GB", {
                year: "numeric",
                month: "short",
                day: "2-digit",
                hour: "2-digit",
                minute: "2-digit"
            });
        },
        formatShortDate(value) {
            if (!value) {
                return "";
            }
            return new Date(value).toLocaleDateString("en-GB", {
                year: "numeric",
                month: "short",
                day: "2-digit"
            });
        },
        buildCover(url) {
            const asset = this.resolveAsset(url);
            return asset ? `linear-gradient(rgba(43, 28, 19, 0.18), rgba(43, 28, 19, 0.18)), url('${asset}')` : "none";
        },
        resolveAsset(url) {
            if (!url) {
                return "https://images.unsplash.com/photo-1518998053901-5348d3961a04?auto=format&fit=crop&w=1200&q=80";
            }
            return url;
        },
        userInitials(value) {
            if (!value) {
                return "HE";
            }
            const parts = String(value).trim().split(/\s+/).slice(0, 2);
            if (parts.length > 1) {
                return parts.map((part) => part.charAt(0).toUpperCase()).join("");
            }
            return value.slice(0, 2).toUpperCase();
        },
        translateCategory(value) {
            if (!value) {
                return "Uncatalogued";
            }
            return categoryMap[value] || value;
        },
        translateNickname(value) {
            if (!value) {
                return "Anonymous Contributor";
            }
            return nicknameMap[value] || value;
        },
        translateText(value) {
            if (!value) {
                return "";
            }
            return textMap[value] || value;
        },
        translateHeritageName(value) {
            if (!value) {
                return "Unnamed heritage item";
            }
            return heritageMap[value] || value;
        },
        translateRegion(value) {
            if (!value) {
                return "Region not specified";
            }
            return regionMap[value] || value;
        },
        translateBackendMessage(message) {
            if (!message) {
                return "";
            }
            return backendMessageMap[message] || message;
        },
        storeUser(user) {
            localStorage.setItem("heritage-current-user", JSON.stringify(user));
        }
    }
}).mount("#app");
