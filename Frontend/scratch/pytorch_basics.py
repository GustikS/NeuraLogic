import torch
x = torch.empty(5, 3)
print(x)

#%%

x = torch.rand(5, 3)
print(x)

#%%

torch.cuda.is_available()

#%%
