import os
import numpy as np
from scipy.signal import convolve2d
from PIL import Image
import sys

def create_gaussian_filter(size):
    sigma = 1.0
    center = size // 2
    x, y = np.meshgrid(np.arange(-center, center+1), np.arange(-center, center+1))
    exponent = -(x**2 + y**2) / (2 * sigma**2)
    gaussian_filter = np.exp(exponent)
    gaussian_filter /= np.sum(gaussian_filter)
    return gaussian_filter

def apply_gaussian_filter(image_path, filter_size, output_directory):
    image = np.array(Image.open(image_path).convert('L'))
    gaussian_filter = create_gaussian_filter(filter_size)
    filtered_image = convolve2d(image, gaussian_filter, mode='same', boundary='symm')

    # Enregistrer l'image filtrée dans le répertoire spécifié
    output_path = os.path.join(output_directory, f"gaussien{filter_size}.png")
    Image.fromarray(filtered_image.astype(np.uint8)).save(output_path)
    print("Image Gaussienne enregistrée avec succès :", output_path)

if __name__ == "__main__":
    image_path = sys.argv[1]
    filter_size = int(sys.argv[2])
    output_directory = sys.argv[3]
    apply_gaussian_filter(image_path, filter_size, output_directory)